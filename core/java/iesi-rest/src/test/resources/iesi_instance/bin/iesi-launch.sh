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
for i in *.jar; do
    classpath="$classpath:$lib_dir/$i"
done

java -cp $classpath io.metadew.iesi.launch.ScriptLauncher "$@"

cd $current_dir
