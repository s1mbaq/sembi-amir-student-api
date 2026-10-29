#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p scripts logs backups src evidence
touch logs/Sembi_Amir_system_report.log
chmod 744 scripts/Sembi_Amir_system_report.sh
./scripts/Sembi_Amir_system_report.sh
cp README.md backups/Sembi_Amir_README.md
cmp README.md backups/Sembi_Amir_README.md
ls -l scripts/Sembi_Amir_system_report.sh backups/Sembi_Amir_README.md
find . -maxdepth 2 -not -path './.git*' -not -path './.gradle*' -not -path './build*' | sort
ps -eo pid,user,comm,args | head -40 || true
pgrep -a java || true
tail -n 5 logs/Sembi_Amir_system_report.log
grep 'Sembi' logs/Sembi_Amir_system_report.log | tee logs/Sembi_Amir_log_analysis.txt
echo '> replaces file contents; >> appends without deleting existing contents.'
