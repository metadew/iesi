# MS Sql Server
## Quickstart

Documentation: https://hub.docker.com/_/microsoft-mssql-server

1. Spin up docker environment

docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=strong(!)password' -e 'MSSQL_PID=Express' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2017-latest-ubuntu 

2. Connect to the docker container

docker exec -it <container> /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P <password>

