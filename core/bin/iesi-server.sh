#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir

lib_dir=$calling_dir/../lib

classpath="*"

cd $lib_dir
for i in *.jar; do
    classpath="$classpath:$lib_dir/$i"
done


cd $lib_dir

java -Dlog4j.configurationFile=$lib_dir/log4j2.xml -cp $classpath io.metadew.iesi.launch.ServerLauncher

cd $calling_dir
