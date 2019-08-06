# Lab1

## Quickstart

1. Spin up docker environment
 
	Open a command prompt in the folder containing this file
	
	 `docker-compose up -d`

2. Copy demo4 data to the instance

* Copy configuration
  * Copy file from conf to conf/
* Copy data
  * Create folder data/demo4
  * Copy files from data to data/demo4
  * Copy data/datasets to data/datasets
* Create metadata repository
  * Run metadata launcher with parameters: -create -type general -ini iesi-demo4.ini
* Load metadata repository
  * Copy files from metadata/ folder to metadata/in/new folder
  * Run metadata launcher with parameters: -load -type general -ini iesi-demo4.ini
