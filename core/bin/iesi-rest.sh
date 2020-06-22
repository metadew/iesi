#!/bin/bash
current_dir=$(pwd)
lib_dir=$current_dir/../rest

cd $lib_dir

java -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest.jar

cd $current_dir