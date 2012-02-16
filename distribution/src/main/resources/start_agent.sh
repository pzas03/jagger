#!/bin/bash

# preparing native libs for agent start

#unzip ./lib/sigar-dist-1.6.0.14.zip -d ./lib/native
#cp ./lib/native/hyperic-sigar-1.6.0.14/sigar-bin/lib/* ./lib/native
#rm -r ./hyperic-sigar-1.6.0.14

$JAVA_HOME/bin/java $1 $2 $3 $4 $5 $6 $7 $8 $9 -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
