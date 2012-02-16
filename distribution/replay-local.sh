#!/bin/bash

./build.sh

cd target/jagger-distribution-1.0.0-SNAPSHOT-distribution-full/jagger-distribution-1.0.0-SNAPSHOT
./start.sh ./profiles/local/environment.properties
cd -