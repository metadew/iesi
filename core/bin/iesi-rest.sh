#!/bin/bash
calling_dir=$(pwd)
current_dir=$(dirname "${BASH_SOURCE[0]}")
lib_dir=$current_dir/../rest

cd $lib_dir

java -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest.jar

cd $calling_dir
