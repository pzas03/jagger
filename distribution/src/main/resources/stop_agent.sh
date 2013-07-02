#!/bin/bash
# deleting all processes with specified name
pgrep -f AgentStarter | xargs -n1 kill -9

