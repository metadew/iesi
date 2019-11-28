#!/usr/bin/bash
CURR_DIR=$(pwd)
SETENV_SCRIPT="$(basename "${BASH_SOURCE[0]}")"
SETENV_DIR=`dirname $0`
IESI_REST_JAR=iesi-rest-0.0.1-SNAPSHOT.thin.jar
cd ${SETENV_DIR}
cd ..
IESI_HOME=$(pwd)
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/bin/iesi-home.conf
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/sbin/iesi-home.conf
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/lib/iesi-home.conf
cp ${IESI_HOME}/conf/log4j2.xml ${IESI_HOME}/lib/log4j2.xml

cd ${CURR_DIR}

#Environment Settings Extensions
. iesi-setenvext.sh