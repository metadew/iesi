#!/bin/bash
docker run --rm --entrypoint bash -it -p 8080:8080 -v /home/hkhattabi/IESI/logs:/opt/iesi/logs --network my-network iesi
