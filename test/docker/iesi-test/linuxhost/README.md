# Linux Host

Many thanks to [https://docs.docker.com/v17.09/engine/examples/running_ssh_service/](https://docs.docker.com/v17.09/engine/examples/running_ssh_service/) for the strategy to apply.

## Setup

1. Build the docker image

`docker build -t iesi-test-linux .`

2. Run the docker image

`docker run -d -P --name iesi-test-linux1 iesi-test-linux`

3. Get the docker port where the ssh server is running

```bash
docker port iesi-test-linux1 22
0.0.0.0:32769
```

4. Now it is possible to ssh to the port on the host with user name *root* and password *iesi*.


## Clean up

1. Stop the docker container

`docker stop iesi-test-linux1`

2. Remove the docker container

`docker rm iesi-test-linux1`

3. Remove the docker image

`docker rmi iesi-test-linux`