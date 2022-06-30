#!/bin/bash
while getopts p: flag
do
  case "${flag}" in
    p) port=${OPTARG}
  esac
done

docker run -v /home/hkhattabi/test_prod:/opt/iesi/logs --rm -d -it --name iesi -p $port iesi