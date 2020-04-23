#!/bin/bash

###################################################
# Author: Peter Billen
#
# Objective: create assemly of gcp terraform config
#
#
###################################################

echo "*****    Initializing script          *****"

current_dir=$(pwd)
script_name=`basename "$0"`
script_dir=`dirname $0`
repo_dir=${script_dir}/../../
workspace_dir=""
remove_instance=0
instance="assembly"
configuration="default"

# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -r|--remove)
        remove_instance=1
        shift
        ;;
        -w=*|--workspace=*)
        workspace_dir="${arg#*=}"
        shift
        ;;
        -i=*|--instance=*)
        instance="${arg#*=}"
        shift
        ;;
        -c=*|--configuration=*)
        configuration="${arg#*=}"
        shift
        ;;
    esac
done

if [ -z "$workspace_dir" ]
then
      echo "No workspace directory has been provided"
      exit 1
fi

echo "workspace=${workspace_dir}"
echo "instance=${instance}"
echo "configuration=${configuration}"
echo "remove instance=${remove_instance}"

#Create the workspace directory by default
mkdir -p ${workspace_dir}/conf
mkdir -p ${workspace_dir}/conf/default
mkdir -p ${workspace_dir}/conf/${configuration}

#Make the instance directory
if [ "${remove_instance}" -eq 1 ]; then
  rm -rf ${workspace_dir}/${instance}
fi
mkdir -p ${workspace_dir}/${instance}

echo "*****    Copy git code                *****"

#use rsync -ax instead of cp -rf to allow exclusions
rsync -ax --exclude ${script_name} ${script_dir} ${workspace_dir}/${instance}

echo "*****    Copy instance configurations *****"

rsync -ax ${workspace_dir}/conf/${configuration}/. ${workspace_dir}/${instance}/

echo "*****    Ready Freddy!!               *****"