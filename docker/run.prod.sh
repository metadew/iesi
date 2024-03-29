#!/bin/bash
while getopts p: flag
do
  case "${flag}" in
    p) port=${OPTARG};;
    *) port=8080;;
  esac
done

docker run -v /home/hkhattabi/IESI/logs:/opt/iesi/logs --rm -d -it  --network my-network --name iesi -p $port:8080 iesi
