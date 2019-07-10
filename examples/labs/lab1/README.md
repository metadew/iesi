# Lab1

## Quickstart

1. Spin up docker environment
 
	Open a command prompt in the folder containing this file
	
	 `docker-compose up -d`

## Steps

* copy file from conf to conf/
* create folder data/lab1
* copy files from data to data/lab1
* run metadata launcher with parameters: -create -type general -ini iesi-lab1.ini
* copy files from metadata/ folder to metadata/in/new folder
* run metadata launcher with parameters: -load -type general -ini iesi-lab1.ini
* start the docker environment
* run script for setting up teams: -ini iesi-lab1.ini -env iesi-lab1 -script lab1.ini-team -paramlist team=team1