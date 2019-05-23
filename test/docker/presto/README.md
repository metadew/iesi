# Presto
## Quickstart

1. Spin up docker environment

	* pull docker image: docker pull starburstdata/presto
	
	* spin up docker environment: docker run -d -p 127.0.0.1:8080:8080 --name presto starburstdata/presto

2. Connect to the docker container

   `docker exec -it presto presto-cli`
   
   http://localhost:8080

