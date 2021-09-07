#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../rest

cd $lib_dir

java -Dspring.main.web-application-type=NONE -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest.jar -launch "$@"

cd $calling_dir
