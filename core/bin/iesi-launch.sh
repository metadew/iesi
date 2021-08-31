#!/bin/bash
current_dir=$(dirname "${BASH_SOURCE[0]}")
lib_dir=$current_dir/../lib

classpath="*"

cd $lib_dir
for i in *.jar; do
    classpath="$classpath:$lib_dir/$i"
done

java -cp $classpath io.metadew.iesi.launch.ExecutionRequestLauncher "$@"

cd $current_dir
