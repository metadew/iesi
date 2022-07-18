#!/bin/bash
docker run --rm --entrypoint bash -it -p 8080:8080 -v /home/hkhattabi/test_dev:/opt/iesi/logs --network=host iesi
