#!/bin/bash
current_file=$(readlink -m $(type -p ${0}))
absolute_dir=`dirname "${current_file}"`
iesi_home=`dirname "${absolute_dir/../}"`

cd $iesi_home/rest/

java -Dlogging.config=./log4j2.xml -Diesi.home=$iesi_home -Dserver.port=8080 -jar iesi-rest.jar --spring.config.name=application,application-ldap --spring.config.location=$iesi_home/rest/config/
