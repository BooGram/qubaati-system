import json, os, re, shutil

SRC = 'postman/Qubaati_System_Full_Flow_Check.postman_collection.json'
OUT = 'bruno/Qubaati_System_Full_Flow_Check'
col = json.load(open(SRC, encoding='utf-8'))

PW = {'adminUsername': 'adminPassword', 'teacherUsername': 'teacherPassword',
      'parentUsername': 'parentPassword', 'studentUsername': 'studentPassword'}

def conv_js(lines):
    """Translate Postman pm.* script API to Bruno's bru/res/test/expect API."""
    txt = '\n'.join(lines)
    txt = txt.replace('pm.collectionVariables.set(', 'bru.setVar(')
    txt = txt.replace('pm.collectionVariables.get(', 'bru.getVar(')
    txt = txt.replace('pm.environment.set(', 'bru.setEnvVar(')
    txt = txt.replace('pm.environment.get(', 'bru.getEnvVar(')
    txt = txt.replace('pm.variables.get(', 'bru.getVar(')
    txt = txt.replace('pm.response.json()', 'res.getBody()')
    txt = txt.replace('pm.response.text()', 'JSON.stringify(res.getBody())')
    txt = txt.replace('pm.response.responseTime', 'res.getResponseTime()')
    txt = re.sub(r'pm\.response\.to\.have\.status\(\s*([^)]*?)\s*\)', r'expect(res.getStatus()).to.equal(\1)', txt)
    txt = txt.replace('pm.response.code', 'res.getStatus()')
    txt = txt.replace('pm.expect(', 'expect(')
    txt = txt.replace('pm.test(', 'test(')
    txt = txt.replace('postman.setNextRequest(', 'bru.runner.setNextRequest(')
    return txt

def indent(txt, n=2):
    pad = ' ' * n
    return '\n'.join((pad + ln if ln.strip() else ln) for ln in txt.split('\n'))

def safe_dir(name):
    return re.sub(r'[<>:"/\\|?*]', '_', name).strip()

def req_auth_block(r):
    a = r.get('auth')
    if a is None:
        return 'inherit', ''
    t = a.get('type')
    if t == 'noauth':
        return 'none', ''
    if t == 'basic':
        u = next((x['value'] for x in a.get('basic', []) if x['key'] == 'username'), '')
        p = next((x['value'] for x in a.get('basic', []) if x['key'] == 'password'), '')
        block = 'auth:basic {\n  username: %s\n  password: %s\n}\n' % (u, p)
        return 'basic', block
    return 'inherit', ''

def get_events(item):
    pre, test = [], []
    for ev in item.get('event', []):
        if ev.get('listen') == 'prerequest':
            pre = ev['script']['exec']
        elif ev.get('listen') == 'test':
            test = ev['script']['exec']
    return pre, test

def write_request(folder_dir, item, seq):
    r = item['request']
    method = r.get('method', 'GET').lower()
    url = r['url'].get('raw') if isinstance(r['url'], dict) else r['url']
    body_raw = r.get('body', {}).get('raw', '') if r.get('body') else ''
    has_body = bool(body_raw.strip())
    auth_kind, auth_block = req_auth_block(r)
    pre, test = get_events(item)

    parts = []
    parts.append('meta {\n  name: %s\n  type: http\n  seq: %d\n}\n' % (item['name'], seq))
    mblock = '%s {\n  url: %s\n' % (method, url)
    if has_body:
        mblock += '  body: json\n'
    else:
        mblock += '  body: none\n'
    mblock += '  auth: %s\n}\n' % auth_kind
    parts.append(mblock)
    if auth_block:
        parts.append(auth_block)
    # headers
    hdrs = [h for h in r.get('header', []) if h.get('key')]
    if not any(h['key'].lower() == 'content-type' for h in hdrs) and has_body:
        hdrs = hdrs + [{'key': 'Content-Type', 'value': 'application/json'}]
    if hdrs:
        hb = 'headers {\n' + ''.join('  %s: %s\n' % (h['key'], h.get('value', '')) for h in hdrs) + '}\n'
        parts.append(hb)
    if has_body:
        parts.append('body:json {\n%s\n}\n' % indent(body_raw, 2))
    if pre:
        parts.append('script:pre-request {\n%s\n}\n' % indent(conv_js(pre), 2))
    if test:
        parts.append('tests {\n%s\n}\n' % indent(conv_js(test), 2))

    fname = '%02d - %s.bru' % (seq, safe_dir(item['name'])[:80])
    open(os.path.join(folder_dir, fname), 'w', encoding='utf-8').write('\n'.join(parts))

# ---- build collection tree ----
if os.path.isdir(OUT):
    shutil.rmtree(OUT)
os.makedirs(OUT)
os.makedirs(os.path.join(OUT, 'environments'))

# bruno.json
open(os.path.join(OUT, 'bruno.json'), 'w', encoding='utf-8').write(json.dumps({
    "version": "1", "name": "Qubaati_System_Full_Flow_Check", "type": "collection",
    "ignore": ["node_modules", ".git"]
}, indent=2))

# collection.bru — collection-level auth (admin, inherited) + the collection pre-request script
coll_pre = []
for ev in col.get('event', []):
    if ev.get('listen') == 'prerequest':
        coll_pre = ev['script']['exec']
coll = []
coll.append('auth {\n  mode: basic\n}\n')
coll.append('auth:basic {\n  username: {{adminUsername}}\n  password: {{adminPassword}}\n}\n')
if coll_pre:
    coll.append('script:pre-request {\n%s\n}\n' % indent(conv_js(coll_pre), 2))
open(os.path.join(OUT, 'collection.bru'), 'w', encoding='utf-8').write('\n'.join(coll))

# environment (Local) — baseUrl + admin creds (no secrets; teacher/parent/student auto-generated per run)
env = 'vars {\n  baseUrl: http://localhost:8080/api/v1\n  adminUsername: admin\n  adminPassword: Admin123!\n}\n'
open(os.path.join(OUT, 'environments', 'Local.bru'), 'w', encoding='utf-8').write(env)

# folders + requests
folder_seq = 0
total_req = 0
for folder in col['item']:
    if 'item' not in folder:
        continue
    folder_seq += 1
    fdir = os.path.join(OUT, '%s' % safe_dir(folder['name']))
    os.makedirs(fdir, exist_ok=True)
    open(os.path.join(fdir, 'folder.bru'), 'w', encoding='utf-8').write(
        'meta {\n  name: %s\n  seq: %d\n}\n' % (folder['name'], folder_seq))
    seq = 0
    for item in folder['item']:
        if 'request' not in item:
            continue
        seq += 1
        total_req += 1
        write_request(fdir, item, seq)

print('Bruno collection written to', OUT)
print('folders:', folder_seq, '| requests:', total_req)
