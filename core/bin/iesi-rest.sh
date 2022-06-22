#!/bin/bash
calling_dir=$(pwd)
relative_dir=$(dirname "${BASH_SOURCE[0]}")
absolute_dir=$calling_dir/$relative_dir
lib_dir=$absolute_dir/../rest

cd $lib_dir

java -Dlogging.config='./log4j2.xml' -Diesi.home=$lib_dir/../ -Dserver.port=8080 -jar iesi-rest.jar --spring.config.name=application,application-ldap --spring.config.location=file://$lib_dir/config/

cd $calling_dir
