#!/bin/bash
set -euo pipefail
until test -s /bootstrap/agent-secret && curl -fsS http://jenkins:8080/jnlpJars/agent.jar -o /tmp/agent.jar; do sleep 3; done
exec java -jar /tmp/agent.jar -url http://jenkins:8080/ -secret @/bootstrap/agent-secret -name sembi-amir-agent -webSocket -workDir /home/jenkins/agent
