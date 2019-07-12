# MS Sql Server

## Quickstart

Documentation: https://hub.docker.com/_/microsoft-mssql-server

1. Spin up docker environment

docker-compose up -d

2. Connect to the docker container

docker exec -it demo2_mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "yourStrong(!)Password"

3. Create database

docker exec -it demo2_mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "yourStrong(!)Password" -i /tmp/setup.sql


## Steps

* copy conf/ files to conf/ folder
* create folder data/demo2
* copy data/demo2 files to data/demo2
* copy data/datasets files to data/datasets
* run metadata launcher with parameters: -create -type general -ini iesi-demo2.ini
* copy files from metadata/ folder to metadata/in/new folder
* run metadata launcher with parameters: -load -type general -ini iesi-demo2.ini
* start the docker environment
* perform database setup
* run cases
