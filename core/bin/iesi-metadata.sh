#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../lib
plugin_lib=$absolute_dir/../plugin_lib

classpath="*"

cd $lib_dir
for i in *.jar; do
    classpath="$classpath:$lib_dir/$i"
done

cd $plugin_lib
for i in *.jar; do
  classpath="$classpath:$plugin_lib/$i"
done

cd $lib_dir

java -cp $classpath io.metadew.iesi.Application -launcher metadata "$@"

cd $calling_dir
