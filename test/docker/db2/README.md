# DB2

## Quickstart

Reference: https://hub.docker.com/r/ibmcom/db2


1. Spin up docker environment

docker run -itd --name mydb2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=<choose> -e DBNAME=testdb -v <db dir>:/database ibmcom/db2

docker run -itd --name mydb2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=password -e DBNAME=testdb -v ./database:/database ibmcom/db2

docker run -itd --name mydb2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=iesi -e DBNAME=iesi -v ./iesi:/database ibmcom/db2

After the docker run command is executed, it will take a couple of minutes for the container to finish setting up. 
You may run docker logs -f <your_container_name> to tail the docker entry point script. 
To confirm Db2 container is ready, in the logs we will see the message Setup has completed.

docker logs -f mydb2

2. Connect to the docker container

docker exec -ti mydb2 bash -c "su - ${DB2INSTANCE}"

docker exec -ti mydb2 bash -c "su - db2inst1"

where ${DB2INSTANCE} is either db2inst1 or the name chosen via the DB2INSTANCE variable


CREATE TABLE IESI.TABLE1 (COL1 INTEGER NOT NULL,COL2 CHAR(25),COL3 VARCHAR(25),COL4 DATE,COL5 DECIMAL(10,2),PRIMARY KEY (COL1))

Check the db2 version: db2level

Set schema -> set schema iesi