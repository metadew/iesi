#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../rest

cd $lib_dir

java -Dlog4j.configurationFile=log4j2.xml -Dspring.profiles.active=single_process -jar iesi-rest.jar

cd $calling_dir
