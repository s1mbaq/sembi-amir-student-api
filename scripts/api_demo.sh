#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")/.."
BASE=http://localhost:8093/api/students
request() {
  local expected="$1"; shift
  local out code
  out=$(mktemp)
  code=$(curl -sS -o "$out" -w '%{http_code}' "$@")
  cat "$out"; echo; echo "HTTP $code expected $expected"
  test "$code" = "$expected"
  BODY=$(cat "$out"); rm "$out"
}
ready() {
  for n in $(seq 1 120); do
    if curl -fsS http://localhost:8093/actuator/health >/dev/null 2>&1; then return; fi
    sleep 2
  done
  echo 'Application did not become healthy'; return 1
}
ready
# Remove only previous demonstration records, preserving other records.
ids=$(curl -fsS "$BASE" | python3 -c 'import json,sys; print(" ".join(str(x["id"]) for x in json.load(sys.stdin) if x["studentNumber"]=="37693"))')
for id in $ids; do request 204 -X DELETE "$BASE/$id"; done
DATA='{"fullName":"Sembi Amir","groupName":"IT2-2307","studentNumber":"37693","email":"amir@example.test"}'
echo 'CREATE'; request 201 -H 'Content-Type: application/json' -d "$DATA" "$BASE"
id=$(printf '%s' "$BODY" | python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])')
echo 'ALL'; request 200 "$BASE"
echo 'ONE'; request 200 "$BASE/$id"
echo 'UPDATE'; request 200 -X PUT -H 'Content-Type: application/json' -d "${DATA/IT2-2307/IT2-2307-updated}" "$BASE/$id"
echo 'PERSISTENCE: remove and recreate containers while preserving named volume'
docker compose down
docker compose up -d
ready
request 200 "$BASE/$id"
printf '%s' "$BODY" | python3 -c 'import json,sys; s=json.load(sys.stdin); assert s["fullName"]=="Sembi Amir" and s["groupName"]=="IT2-2307-updated"; print("Persisted values verified")'
echo 'DELETE'; request 204 -X DELETE "$BASE/$id"
echo 'NOT FOUND'; request 404 "$BASE/$id"
echo 'INVALID INPUT'; request 400 -H 'Content-Type: application/json' -d '{}' "$BASE"
echo 'All PostgreSQL CRUD checks passed'
