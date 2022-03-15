#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../lib

classpath="*"

cd $lib_dir
for i in *.jar; do
    classpath="$classpath:$lib_dir/$i"
done


java -Dlog4j.configurationFile=$lib_dir/log4j2.xml -cp $classpath io.metadew.iesi.launch.ExecutionLauncher "$@"

cd $calling_dir