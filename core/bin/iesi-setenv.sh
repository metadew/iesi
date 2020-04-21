#!/usr/bin/bash
CURR_DIR=$(pwd)
SETENV_SCRIPT="$(basename "${BASH_SOURCE[0]}")"
SETENV_DIR=`dirname $0`
IESI_REST_JAR=iesi-rest-0.0.1-SNAPSHOT.thin.jar
cd ..
IESI_HOME=$(pwd)
cp ${IESI_HOME}/conf/iesi-log4j2-cli.xml ${IESI_HOME}/lib/log4j2.xml
cp ${IESI_HOME}/conf/iesi-log4j2-cli.xml ${IESI_HOME}/rest/log4j2.xml

cd ${CURR_DIR}

#Environment Settings Extensions
. iesi-setenvext.sh
