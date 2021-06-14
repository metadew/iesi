#!/bin/bash
current_dir=$(dirname "${BASH_SOURCE[0]}")
lib_dir=$current_dir/../rest

cd $lib_dir

java -Dlog4j.configurationFile=log4j2.xml -Dloader.path="../plugins" -Dloader.main=io.metadew.iesi.server.rest.Application -cp iesi-rest.jar org.springframework.boot.loader.PropertiesLauncher

cd $current_dir
