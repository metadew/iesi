# DB2

## Quickstart

Reference: https://hub.docker.com/r/ibmcom/db2


1. Spin up docker environment

docker run -itd --name demo4_fotg_db2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=fotgpass -e DBNAME=fotg -v ./data:/database ibmcom/db2

2. Verify successful start

docker logs -f demo4_fotg_db2

3. Connect to the docker container

docker exec -ti demo4_fotg_db2 bash -c "su - db2inst1"

set schema FOTG

CREATE TABLE FOTG.CUSTOMERS (ID INTEGER NOT NULL,DateJoined DATE,BirthDate DATE,FirstName VARCHAR(255),LastName VARCHAR(255),Phone VARCHAR(255),Street VARCHAR(255),City VARCHAR(255),Country VARCHAR(255),Gender VARCHAR(255),Email VARCHAR(255),PRIMARY KEY (ID))

CREATE TABLE FOTG.RESTAURANTS (ID INTEGER NOT NULL,DateJoined DATE,Name VARCHAR(255),Street VARCHAR(255),City VARCHAR(255),Country VARCHAR(255),Email VARCHAR(255),PRIMARY KEY (ID))

CREATE TABLE ORDERS (ID	INTEGER NOT NULL,CustomerID INTEGER NOT NULL,RestaurantID INTEGER NOT NULL,OrderDate DATE,Price INTEGER,PRIMARY KEY (ID))

docker stop demo4_fotg_db2