#!/bin/bash

# -------------- killing prev processes
target=$1
export target

# deleting all processes with specified name
ps axwww | grep $target | awk '{print $1}' | xargs -n1 kill -9


rm -rf /home/jagger-ci/jaggerdb

# -------------- run

