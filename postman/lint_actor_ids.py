import json, re
P = 'postman/Qubaati_System_Full_Flow_Check.postman_collection.json'
col = json.load(open(P, encoding='utf-8'))

def walk(items, folder=None):
    for it in items:
        if 'item' in it:
            yield from walk(it['item'], it['name'])
        else:
            yield folder, it

NORMAL = lambda f: not (f.startswith('12') or f.startswith('13') or f.startswith('14'))
# actor-id tokens that must NOT appear as the CURRENT user in normal path/body
ACTOR_PATH = [r'/teachers/\d', r'/parents/\d', r'teacherId=', r'parentId=', r'studentId=', r'assignedByTeacher']
BODY_ACTOR = ['"teacherId"', '"parentId"', '"assignedByTeacherId"', '"assignedByTeacher"']
MANUAL_ENDPOINTS = ['/learning-styles', '/learning-style-history', '/skill-progress-history']

folders = {}
total = 0
violations = []
for folder, req in walk(col['item']):
    total += 1
    folders.setdefault(folder, 0); folders[folder] += 1
    if not NORMAL(folder): continue
    r = req.get('request', {})
    method = r.get('method', '')
    url = r.get('url', {}); raw = url.get('raw','') if isinstance(url, dict) else str(url)
    body = r.get('body', {}).get('raw','') if r.get('body') else ''
    name = req['name']
    # 1. actor id in path/query
    for pat in ACTOR_PATH:
        if re.search(pat, raw):
            violations.append((folder, name, 'actor-id-in-path: %s  [%s]' % (pat, raw)))
    # 2. actor id in body
    for tok in BODY_ACTOR:
        if tok in body:
            violations.append((folder, name, 'actor-id-in-body: %s' % tok))
    # 3. body studentId: allowed as a resource TARGET on enroll/assign endpoints; a violation only when it
    #    would represent the CURRENT student (i.e. a student-auth request carrying its own id).
    TARGET_EP = ('assign-student', '/classrooms/students/enroll', '/classrooms/students/remove', '/activity-assignments')
    if '"studentId"' in body and not any(t in raw for t in TARGET_EP):
        violations.append((folder, name, 'studentId-in-body (not a resource target): %s' % raw))
    # 4. manual analytics endpoints (POST = creation)
    for ep in MANUAL_ENDPOINTS:
        if ep in raw and method == 'POST':
            violations.append((folder, name, 'manual analytics POST: %s' % raw))
    # 5. manual question creation by teacher (POST /questions). Admin/debug rename carries a marker.
    if raw.rstrip('/').endswith('/questions') and method == 'POST':
        a = r.get('auth', {})
        u = ''
        if a.get('type') == 'basic':
            u = next((x['value'] for x in a['basic'] if x['key']=='username'), '')
        if 'admin' not in u.lower() and 'ADMIN' not in name and 'DEBUG' not in name:
            violations.append((folder, name, 'manual POST /questions (non-admin): auth=%s' % u))

print('JSON valid. folders=%d requests=%d' % (len(folders), total))
print('--- normal-folder lint ---')
if not violations:
    print('  0 violations in normal folders.')
else:
    for v in violations: print('  VIOLATION', v)
print('--- folder request counts ---')
for f in sorted(folders): print('  %-55s %d' % (f, folders[f]))
