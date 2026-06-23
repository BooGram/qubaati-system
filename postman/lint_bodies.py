import os, re, json
ROOT = 'src/main/java/com/example/qubaatisystem'
CTRL = ROOT + '/Controller'
DTOIN = ROOT + '/DTO/In'
ENUMD = ROOT + '/Enum'
COL = 'postman/Qubaati_System_Full_Flow_Check.postman_collection.json'

# ---- 1. enums: name -> [values] ----
enums = {}
for f in os.listdir(ENUMD):
    if not f.endswith('.java'):
        continue
    s = open(os.path.join(ENUMD, f), encoding='utf-8').read()
    m = re.search(r'enum\s+(\w+)\s*\{(.*?)\}', s, re.S)
    if m:
        body = m.group(2)
        vals = re.findall(r'\b([A-Z][A-Z0-9_]+)\b', body.split(';')[0])
        enums[m.group(1)] = vals

# ---- 2. InDTO field maps: dto -> {field: {type, required, enum}} ----
def parse_dto(path):
    s = open(path, encoding='utf-8').read()
    # strip block comments
    s = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    fields = {}
    ann = []
    for line in s.splitlines():
        t = line.strip()
        if t.startswith('//'):
            continue
        if t.startswith('@'):
            ann.append(t)
            continue
        fm = re.match(r'private\s+([\w<>.]+)\s+(\w+)\s*;', t)
        if fm:
            typ, name = fm.group(1), fm.group(2)
            required = any(a.startswith(('@NotNull', '@NotBlank', '@NotEmpty')) for a in ann)
            enum = typ if typ in enums else None
            fields[name] = {'type': typ, 'required': required, 'enum': enum}
            ann = []
        elif t and not t.startswith(('import', 'package', '@', 'public', 'private static', '}')):
            ann = []
    return fields
dtos = {}
for f in os.listdir(DTOIN):
    if f.endswith('.java'):
        dtos[f[:-5]] = parse_dto(os.path.join(DTOIN, f))

# ---- 3. routes: (method, path) -> {dto, hasBody, bodyRequired} ----
mp = re.compile(r'@(Get|Post|Put|Delete|Patch)Mapping\s*(?:\(\s*(?:value\s*=\s*)?"([^"]*)"\s*\))?')
rm = re.compile(r'@RequestMapping\s*\(\s*"([^"]*)"\s*\)')
body_re = re.compile(r'@RequestBody(\(\s*required\s*=\s*false\s*\))?\s+([A-Za-z0-9_]+)\s+\w+')
routes = {}
for f in sorted(os.listdir(CTRL)):
    if not f.endswith('.java'):
        continue
    s = open(os.path.join(CTRL, f), encoding='utf-8').read()
    base = (rm.search(s).group(1) if rm.search(s) else '')
    idxs = [(m.start(), m.group(1).upper(), m.group(2) or '') for m in mp.finditer(s)]
    for i, (start, verb, sub) in enumerate(idxs):
        end = idxs[i+1][0] if i+1 < len(idxs) else len(s)
        block = s[start:end]
        full = (base + sub).replace('//', '/')
        bm = body_re.search(block)
        routes[(verb, full)] = {
            'dto': (bm.group(2) if bm else None),
            'bodyRequired': bool(bm) and not bm.group(1),
            'file': f[:-5],
        }

# ---- 4. walk Postman, validate bodies ----
col = json.load(open(COL, encoding='utf-8'))
def walk(items, folder=None):
    for it in items:
        if 'item' in it:
            yield from walk(it['item'], it['name'])
        else:
            yield folder, it

issues = []
def add(folder, name, kind, detail):
    issues.append((kind, folder, name, detail))

for folder, req in walk(col['item']):
    r = req.get('request', {})
    method = r.get('method', 'GET')
    url = r.get('url', {})
    raw = url.get('raw', '') if isinstance(url, dict) else str(url)
    if not raw.startswith('{{baseUrl}}'):
        continue
    pathp = raw[len('{{baseUrl}}'):].split('?', 1)[0]
    full = '/api/v1' + pathp
    route = routes.get((method, full))
    if route is None:
        continue  # lint handles unknown routes
    body_raw = r.get('body', {}).get('raw', '').strip() if r.get('body') else ''
    dto = route['dto']
    if not dto:
        # route takes no body; a stray body is only a minor note (skip)
        continue
    fields = dtos.get(dto, {})
    # parse body JSON (protect {{vars}})
    if not body_raw:
        if route['bodyRequired']:
            add(folder, req['name'], 'MISSING_BODY', '%s %s expects %s' % (method, full, dto))
        continue
    # Postman substitutes {{var}} at runtime; for static parse, replace with 1 (valid as a number value and
    # valid concatenated inside a string literal like "faisal.student{{runSuffix}}").
    tmp = re.sub(r'\{\{[^}]+\}\}', '1', body_raw)
    try:
        obj = json.loads(tmp)
    except Exception as e:
        add(folder, req['name'], 'INVALID_JSON', '%s : %s :: BODY=%s' % (full, str(e)[:50], body_raw[:80]))
        continue
    if not isinstance(obj, dict):
        add(folder, req['name'], 'BODY_NOT_OBJECT', full)
        continue
    present = set(obj.keys())
    # required fields present?
    neg = bool(re.search(r'\b(401|403|expect|Cannot|Fails|Blocked)\b', req['name']))
    missing = [fn for fn, meta in fields.items() if meta['required'] and fn not in present]
    if missing:
        add(folder, req['name'], 'NEG_MISSING' if neg else 'MISSING_REQUIRED',
            '%s missing %s | present=%s' % (full, missing, sorted(present)))
    # update endpoints must carry id
    if full.endswith('/update') and 'id' in fields and 'id' not in present:
        add(folder, req['name'], 'UPDATE_NO_ID', full)
    # unknown field names (typos)
    for k in present:
        if k not in fields:
            add(folder, req['name'], 'UNKNOWN_FIELD', '%s has "%s" not in %s' % (full, k, dto))
    # enum value validity (only literal values, not {{vars}})
    for k, v in obj.items():
        if k in fields and fields[k]['enum'] and isinstance(v, str) and not v.startswith('__V_'):
            allowed = enums[fields[k]['enum']]
            if v not in allowed:
                add(folder, req['name'], 'BAD_ENUM', '%s "%s"="%s" not in %s%s' % (full, k, v, fields[k]['enum'], allowed))

# ---- report ----
from collections import Counter
print('routes=%d  dtos=%d  enums=%d' % (len(routes), len(dtos), len(enums)))
c = Counter(i[0] for i in issues)
print('issue counts:', dict(c) or '{}')
print('TOTAL BODY ISSUES:', len(issues))
for kind in ['MISSING_BODY', 'INVALID_JSON', 'BODY_NOT_OBJECT', 'MISSING_REQUIRED', 'UPDATE_NO_ID', 'UNKNOWN_FIELD', 'BAD_ENUM']:
    rows = [i for i in issues if i[0] == kind]
    if rows:
        print('\n--- %s (%d) ---' % (kind, len(rows)))
        for _, folder, name, detail in rows[:60]:
            print('   [%s] %s :: %s' % (folder[:18], name[:42], detail))
