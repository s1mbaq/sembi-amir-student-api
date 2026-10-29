#!/bin/bash
set -euo pipefail
ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
mkdir -p "$ROOT/logs"
{
  echo 'Student: Sembi Amir | Group IT2-2307 | ID 37693'
  printf 'Date: '; date --iso-8601=seconds
  printf 'User: '; whoami
  echo 'Operating system:'; cat /etc/os-release
  echo 'Kernel:'; uname -a
  echo 'CPU:'; lscpu
  echo 'Memory:'; free -h
  echo 'Disk usage:'; df -h
  echo 'Available disk space:'; df -h --output=source,avail,target
} | tee "$ROOT/logs/Sembi_Amir_system_report.log"
