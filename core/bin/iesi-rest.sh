#!/bin/bash
current_dir=$(pwd)
script_dir=`dirname $0`

cd $script_dir
script_dir=$(pwd)
lib_dir=$script_dir/../rest

#Environment Settings
. iesi-setenv.sh

cd $lib_dir

java -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest-0.1.0.jar

cd $current_dir