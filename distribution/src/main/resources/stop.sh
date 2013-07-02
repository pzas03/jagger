#!/bin/bash
# deleting all processes with specified name
pgrep -f JaggerLauncher | xargs -n1 kill -9

