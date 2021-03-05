#!/bin/bash

###################################################
# Author: Peter Billen
#
# Objective: build a branch from git and assemble
# the distribution
#
###################################################


echo "*****    Initializing script          *****"

#Debug settings
fetch_from_git=1
maven_build_ext=1
iesi_build_src=1
iesi_workspace_setup=1
iesi_assembly=1
iesi_distribution=1
exclude=""

#Script settings
iesi_version=0.5.0
branch="develop"
branch_folder=$(echo $branch | sed -e 's/\//./g')
current_dir=$(pwd)
script_dir=`dirname $0`
build_dir=${current_dir}
source_dir=${build_dir}/source
branch_dir=${source_dir}/${branch_folder}
repo_dir=${branch_dir}/iesi


# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -d=*|--dir=*)
        build_dir="${arg#*=}"
        shift
        ;;
        -b=*|--branch=*)
        branch="${arg#*=}"
        shift
        ;;
        -e=*|--exclude=*)
        exclude="${arg#*=}"
        shift #g for git, m for maven, b for build, w for workspace, a for assembly,d for distribution
        ;;
    esac
done

#Exclude debug settings if needed
if [[ $exclude == *"g"* ]]; then
  fetch_from_git=0
  echo "Exclude fetch from git"
fi
if [[ $exclude == *"m"* ]]; then
  maven_build_ext=0
  echo "Exclude maven extension build"
fi
if [[ $exclude == *"b"* ]]; then
  iesi_build_src=0
  echo "Exclude build from source"
fi
if [[ $exclude == *"w"* ]]; then
  iesi_workspace_setup=0
  echo "Exclude workspace setup"
fi
if [[ $exclude == *"a"* ]]; then
  iesi_assembly=0
  echo "Exclude assembly"
fi
if [[ $exclude == *"d"* ]]; then
  iesi_distribution=0
  echo "Exclude distribution creation"
fi

echo "*****    Installing Pre-requisites    *****"

echo "*****    Update packages              *****"
sudo apt-get update

echo "*****    Installing Git               *****"
PKG_OK=$(dpkg-query -W --showformat='${Status}\n' git 2>/dev/null | grep "install ok installed")
if [ "" == "$PKG_OK" ]; then
  sudo apt-get --force-yes --yes install git
else
  echo "Skipping, Git is already installed"
fi

echo "*****    Installing Rsync             *****"
PKG_OK=$(dpkg-query -W --showformat='${Status}\n' rsync 2>/dev/null | grep "install ok installed")
if [ "" == "$PKG_OK" ]; then
  sudo apt-get --force-yes --yes install rsync
else
  echo "Skipping, Rsync is already installed"
fi

echo "*****    Installing OpenJDK           *****"
PKG_OK=$(dpkg-query -W --showformat='${Status}\n' openjdk-8-jdk 2>/dev/null | grep "install ok installed")
if [ "" == "$PKG_OK" ]; then
  echo "Installing OpenJDK-8"
  sudo apt-get --force-yes --yes install openjdk-8-jdk
else
  echo "Skipping, OpenJDK-8 is already installed"
fi

echo "*****    Installing Maven             *****"
PKG_OK=$(dpkg-query -W --showformat='${Status}\n' maven 2>/dev/null | grep "install ok installed")
if [ "" == "$PKG_OK" ]; then
  echo "Installing Maven"
  sudo apt-get --force-yes --yes install maven
else
  echo "Skipping, Maven is already installed"
fi

echo "*****    Installing Pre-requisites    *****"

echo "*****    Fetch branch from GIT        *****"


if [ "$fetch_from_git" -eq 1 ]; then
  
  mkdir -p ${source_dir}
  
  echo "Fetching branch from git"
  rm -rf ${branch_dir}
  mkdir -p ${branch_dir}
  cd ${branch_dir}
  git clone -b ${branch} --single-branch https://github.com/metadew/iesi.git
else
  echo "Skipping, branch fetch from git"
  cd ${branch_dir}
fi
cd iesi


echo "*****    Installing build ext         *****"
if [ "$maven_build_ext" -eq 1 ]; then
  echo "Installing build ext"
  cd ${repo_dir}/build/ext
  while IFS='|' read -r file groupid artifactid version packaging
  do
    echo "Installing ${file} into local Maven repository"
    mvn install:install-file -Dfile=$file -DgroupId=$groupid -DartifactId=$artifactid -Dversion=$version -Dpackaging=$packaging
  done < "${repo_dir}/build/solution/ext.list"
else
  echo "Skipping, build ext installation"
fi


if [ "$iesi_build_src" -eq 1 ]; then
  echo
  echo "*****    Building iesi-core           *****"
  cd ${repo_dir}/core/java/iesi-core
  mvn clean install project-info-reports:dependencies -Pdependencies

  echo
  echo "*****    Building iesi-rest           *****"
  cd ${repo_dir}/core/java/iesi-rest-without-microservices
  mvn clean package project-info-reports:dependencies

  #Skipping since it is not actively used at this stage
  #echo
  #echo "*****    Building iesi-test           *****"
  #cd ${repo_dir}/core/java/iesi-test
  #mvn clean install project-info-reports:dependencies
  #-Pdependencies TODO check if needed to add dependencies profile
else
  echo "Skipping, iesi source build"
fi


if [ "$iesi_workspace_setup" -eq 1 ]; then
  echo
  echo "*****    Setting up workspace         *****"
  mkdir -p ${build_dir}/workspace
  mkdir -p ${build_dir}/workspace/dist
  
  workspace_dir=${build_dir}/workspace/conf/${iesi_version}/build
  rm -rf ${workspace_dir}
  mkdir -p ${workspace_dir}
else
  echo "Skipping, iesi workspace setup"
fi

jar_file=${repo_dir}/core/java/iesi-core/target/iesi-core-${iesi_version}-jar-with-dependencies.jar

if [ "$iesi_assembly" -eq 1 ]; then
  echo
  echo "*****    Running assembly             *****"
  java -cp ${jar_file} io.metadew.iesi.launch.AssemblyLauncher -repository ${repo_dir} -sandbox ${build_dir}/workspace -instance build -version ${iesi_version}
else
  echo "Skipping, iesi assembly"
fi

if [ "$iesi_distribution" -eq 1 ]; then
  echo
  echo "*****    Creating distribution        *****"
  mkdir -p ${build_dir}/workspace/dist/${branch_folder}
  
  #Generate uuid
  uuid=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
  #make it lowercase
  uuid=${uuid,,}
  
  #copy sources
  rm -rf ${build_dir}/workspace/dist/${branch_folder}/${uuid}
  mkdir -p ${build_dir}/workspace/dist/${branch_folder}/${uuid}/iesi
  rsync -ax ${build_dir}/workspace/${iesi_version}/build/. ${build_dir}/workspace/dist/${branch_folder}/${uuid}/iesi/
  
  cd ${build_dir}/workspace/dist/${branch_folder}/${uuid}
  now=$(date +"%Y%m%d%H%M%S%N")
  tar -zcf ${build_dir}/workspace/dist/${branch_folder}/iesi-dist-${now}.tar.gz .
  cd ${current_dir}
  
  #cleanup
  rm -rf ${build_dir}/workspace/dist/${branch_folder}/${uuid}
else
  echo "Skipping, distribution creation"
fi


echo "*****    Ready Freddy!!                 *****"