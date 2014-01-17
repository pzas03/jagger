#!/bin/bash

# Required software:
# 1. Graphviz package (version 2.26.3 or higher) : http://www.graphviz.org/Download_linux_rhel.php
# 2. Doxygen (version 1.8.4 or higher): http://www.stack.nl/~dimitri/doxygen/download.html
# 3. xsddoc (version 1.0): http://nixbit.com/cat//documentation/xsddoc/

version=$1

# set version - don't change text (doxygen will use it)
echo "Version: $version"
echo "Version: $version" > ./doc/setup/JaggerVersion.txt

# xsd schema
rm -r ./doc/html/xsd/*.*
xsddoc -q -t "Jagger XML schema" -o ./doc/html/xsd/ ./chassis/spring.schema/src/main/resources/config-schema.xsd

# main docu
rm `ls ./doc/html/*.* | grep -v "header.html" | grep -v "tab-panel.css" | grep -v "tab-panel.js"`
doxygen ./doc/setup/Doxyfile

# copy docu for later usage by gh-pages branch script
if [ ! -z "$version" ]; then
    echo "Docu copy for version '$version' is created"
    cp -r ./doc/html ./doc_$version
fi