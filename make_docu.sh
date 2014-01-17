#!/bin/bash

# Required software:
# 1. Graphviz package (version 2.26.3 or higher) : http://www.graphviz.org/Download_linux_rhel.php
# 2. Doxygen (version 1.8.4 or higher): http://www.stack.nl/~dimitri/doxygen/download.html
# 3. xsddoc (version 1.0): http://nixbit.com/cat//documentation/xsddoc/

# xsd schema
rm -r ./doc/html/xsd/*.*

xsddoc -q -t "Jagger XML schema" -o ./doc/html/xsd/ ./chassis/spring.schema/src/main/resources/config-schema.xsd

# main docu
rm `ls ./doc/html/*.* | grep -v "header.html" | grep -v "tab-panel.css" | grep -v "tab-panel.js"`
#rm ./doc/html/*.*

doxygen ./doc/setup/Doxyfile

