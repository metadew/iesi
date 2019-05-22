# Drill
## Quickstart

1. Spin up docker environment

	* spin up docker environment: docker run -i --name drill-1.16.0 -p 8047:8047 --detach -t drill/apache-drill:1.16.0 /bin/bash
	
	* for jdbc expose drillbit port 31010: docker run -i --name drill-1.16.0 -p 8047:8047 -p 31010 --detach -t drill/apache-drill:1.16.0 /bin/bash

2. Connect to the docker container

   `docker exec -it drill-1.16.0 bash`
   
   /opt/drill/bin/drill-localhost
   
   http://localhost:8047

SELECT version FROM sys.version;
 SELECT first_name, last_name FROM cp.`employee.json` LIMIT 1;
 