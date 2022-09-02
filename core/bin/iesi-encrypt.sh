#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../lib

classpath="*"

cd $lib_dir

java -jar iesi-core.jar -launcher encryption "$@"

cd $calling_dir
