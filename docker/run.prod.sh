#!/bin/bash
while getopts p: flag
do
  case "${flag}" in
    p) port=${OPTARG};;
    *) port=8080;;
  esac
done

docker run -v /home/hkhattabi/test_prod:/opt/iesi/logs --rm -d -it  --network=host --name iesi -p $port:8080 iesi
