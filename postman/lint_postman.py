"""Postman lint for the Tahadaw-style refactor.
Checks (target = 0 violations):
  1. No path variables in request paths  ({{x}} as a whole path segment).
  2. No actor-id fields in request bodies (teacherId / parentId / assignedByTeacher[Id] / userId as actor).
  3. Every request resolves to a real backend route (method + path) from the compiled controllers.
Resource-target ids in bodies (id, studentId, classroomId, activityId, submissionId, assignmentId, ...) are allowed.
"""
import json, re, os, sys

COL = 'postman/Qubaati_System_Full_Flow_Check.postman_collection.json'
col = json.load(open(COL, encoding='utf-8'))

# Build the authoritative backend route set directly from the compiled controllers (self-contained, never stale).
CTRL = 'src/main/java/com/example/qubaatisystem/Controller'
_map = re.compile(r'@(Get|Post|Put|Delete|Patch)Mapping\s*(?:\(\s*(?:value\s*=\s*)?"([^"]*)"\s*\))?')
_rm = re.compile(r'@RequestMapping\s*\(\s*"([^"]*)"\s*\)')
route_set = set()
for f in sorted(os.listdir(CTRL)):
    if not f.endswith('.java'):
        continue
    s = open(os.path.join(CTRL, f), encoding='utf-8').read()
    base = (_rm.search(s).group(1) if _rm.search(s) else '')
    for m in _map.finditer(s):
        route_set.add((m.group(1).upper(), (base + (m.group(2) or '')).replace('//', '/')))

ACTOR_BODY = ['"teacherId"', '"parentId"', '"assignedByTeacher"', '"assignedByTeacherId"', '"userId"', '"adminId"']

def walk(items, folder=None):
    for it in items:
        if 'item' in it:
            yield from walk(it['item'], it['name'])
        else:
            yield folder, it

pathvar_viol, actor_viol, route_viol = [], [], []
folders, total = {}, 0
for folder, req in walk(col['item']):
    total += 1
    folders[folder] = folders.get(folder, 0) + 1
    r = req.get('request', {})
    method = r.get('method', 'GET')
    url = r.get('url', {})
    raw = url.get('raw', '') if isinstance(url, dict) else str(url)
    if not raw.startswith('{{baseUrl}}'):
        continue
    rest = raw[len('{{baseUrl}}'):]
    pathp, _, query = rest.partition('?')
    for seg in pathp.split('/'):
        if re.fullmatch(r'\{\{[^}]+\}\}', seg):
            pathvar_viol.append((folder, req['name'], seg, pathp))
    body = r.get('body', {}).get('raw', '') if r.get('body') else ''
    for tok in ACTOR_BODY:
        if tok in body:
            actor_viol.append((folder, req['name'], tok))
    full = '/api/v1' + pathp
    if (method, full) not in route_set:
        route_viol.append((folder, req['name'], method, full))

print('Postman JSON valid. folders=%d requests=%d  backend routes=%d' % (len(folders), total, len(route_set)))
print('--- 1. path-variable violations:', len(pathvar_viol))
for v in pathvar_viol[:40]: print('   PATHVAR', v)
print('--- 2. actor-id-in-body violations:', len(actor_viol))
for v in actor_viol[:40]: print('   ACTOR', v)
print('--- 3. requests not matching any backend route:', len(route_viol))
for v in route_viol[:60]: print('   NOROUTE', v[2], v[3], ' [%s]' % v[1])
tot = len(pathvar_viol) + len(actor_viol) + len(route_viol)
print('=== TOTAL VIOLATIONS:', tot, '===')
sys.exit(1 if tot else 0)
