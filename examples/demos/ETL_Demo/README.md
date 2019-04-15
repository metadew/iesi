# ETL Demo
## Quickstart

1. Spin up docker environment
 
	Open a command prompt in the folder containing this file
	
	 `docker-compose up -d`

2. Open the PGAdmin web-based Postgres client

	- url: http://localhost:3006
	- login: user@enterprise.com
	- pass: pgadminpass


	You can connect to the 3 Demo-servers with these passwords:
	- source-CRM : crmpass
	- target-db : targetpass
	- target-db-Acceptance : targetpass

3. Connect to the IESI docker container

   `docker exec -it iesi /bin/bash`

4. Execute the ETL script
	```
	cd v0.0.1/bin
	./iesi-launch.sh -script ordersETL -env dev
	```
	The script will start and as part of the first action, poll the CRM db and wait for a record of today in the exports database. As defined in the script file, it will check every 20 seconds during a time window of 10 minutes.
	
	 Insert a record via PGAdmin: click on the CRM db and then in the menu tools > querytool.
	 
	 `INSERT INTO exports (export_date, orders_file, products_file) VALUES ('2019-04-08', 'orders8871.csv', 'products7789.csv')`
	 
	 *! replace the date with the current day*

	The script will detect this new line within the next 20 seconds and proceed with the execution of the ETL job.
	
5. Verify the result

  - Via PGAdmin, you can now check the results table in the target-db has been populated
  - In the targetdata folder you will now find the Exel file with the result data

6. Run again for another environment

	`./iesi-launch.sh -script ordersETL -env acc`
	
	You can now similarly verify the results table of the target-db-acc is populated.

7. Bring docker environment down
 
	Open a command prompt in the folder containing this file
	
	 `docker-compose down`
	 
## Demo setup

The demo has been packaged fully into a multi-container docker set. These docker containers simulate the various systems that make a typical enterprise landscape.

The landscape consists of a Linux box which will serve as both the source and target unix systems, one Postgres source database and 2 target databases simulating 2 environments.

On top of these 4 containers, Iesi itself will also run on a separate Linux container and lastly a Postgres client (PGAdmin) is also included as a container to give webbased access to the Postgres databases.

![
](doc/architecture.png)

## Configuration and included content

**docker-compose.yml**

This file defines the containers that make up our simulated landscape

**crmdb/**

This folder holds the Dockerfile to build a Postgres db, initialized with one table 'Exports' and one dummy row.

**iesi/**

The Iesi container is an Ubuntu image, enriched with  
- Java8  
- an Iesi release with a repository  
- a Postgres JDBC driver  
- Pentaho binaries,
- the Pentaho ETL job created for this Demo (basic_join.ktr)
- in metadata:
  - Predefined definitions of all connections in our landscape  
  - A script that will execute the 5 steps of the demo

When starting up the instance, it will load the definitions of connections and the script in so the container is fully ready to execute scripts.

**pgadmin/**

The PGAdmin webclient to interact with the databases. The connections to our 3 databases are preconfigured.

**sourcedata/** and **targetdata/**

Docker compose will spin up a simple SFTP container which represents Unix filesystems in our landscape. Two volumes are linked to this container to mimic 2 servers: sourcedata and targetdata.

The sourcedata volume is pre-filled with the sourcefiles we will use in the demo.

The target volume is also linked to a local folder so you do not necessarily have to FTP to see the results.

**/targetdb**

This folder holds the Dockerfile to build the target Postgres databases, initialized with the empty table 'results'. The docker compose file will use this configuration twice, to spin up databases for different environments.

## The demo explained

The demo showcases the orchestration power of Iesi to execute an ETL script connecting to various systems and environments.

![
](doc/script.png)

The script "ordersETL" as defined in file 301-SCRIPT-ETL_Demo.json, will perform the following steps:

1.  SCHEDULING  

    Query the source database, and poll for a record in the 'exports' table winch has export_date equal to TODAY. If this record is not yet present, the script will wait until it arrives. Once there, it will read out the row containing the filenames of the 2 sourcefiles we want to get.  
	> Demonstrated functionality: db connection, scheduling, capturing parameters
    
2.  FLAT FILE RETRIEVAL (orders) &
3.  FLAT FILE RETRIEVAL (products)

    The script will create a connection to the Unix host and get the files of which the names were returned in step 1. It will transfer these to the Iesi host.  
    
	> Demonstrated functionality: OS connection, file transfer, use of parameters
    
4.  EXECUTE JOB  

    The script will start an ETL job designed in Pentaho that will join the 2 files and put the result both in an Excel file as store it in the target database. Iesi will make sure the job is ran using the correct files and connecting to the right database.  
    
    ![
](doc/ETL.png)
	> Demonstrated functionality: connection parameter lookup, use of parameters for external process
    
5.  PUBLISH RESULT FILE  

    The script will publish the Excel file to the target file area with a timestamped name.  
	> Demonstrated functionality: outwards file transfer, use of generators (date)
