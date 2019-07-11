#!/bin/bash
current_dir=$(pwd)
script_dir=`dirname $0`

cd $script_dir
script_dir=$(pwd)
lib_dir=$script_dir/../lib

#Environment Settings
. iesi-setenv.sh

classpath="*"

cd $lib_dir

java "-Dloader.path=." -jar ./$IESI_REST_JAR

cd $current_dir
