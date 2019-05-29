#!/usr/bin/bash
CURR_DIR=$(pwd)
SETENV_SCRIPT="$(basename "${BASH_SOURCE[0]}")"
SETENV_DIR=`dirname $0`
cd ${SETENV_DIR}
cd ..
IESI_HOME=$(pwd)
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/bin/iesi-home.conf
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/sbin/iesi-home.conf
echo -e "iesi.home=${IESI_HOME}" >${IESI_HOME}/lib/iesi-home.conf
cd ${CURR_DIR}

#Environment Settings Extensions
. iesi-setenvext.sh