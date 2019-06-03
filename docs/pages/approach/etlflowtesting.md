{% include navigation.html %}

# ETL flow testing

This page highlights several options for testing ETL flows.

## Getting started

Starting ETL flow testing is possible in small steps: first, we will validate if ETL flows execute; then we will add additional steps after 
which we will introduce validations. It will be these validations that we will scale and grow fast using configuration based automation components. 

### Simple ETL flow execution

Is it possible to run the ETL flow?
* cli.executeCommand

### Add the necessary cleaning steps

Use delete/truncate statements to perform the necessary cleaning upfront:
* sql.executeStatement (one or multiple statements can be executed)
* cli.executeCommand

### Add the necessary initialization steps

Insert relevant initialization values in tables or configuration files
* sql.executeStatement (one or multiple statements can be executed)
* cli.executeCommand

### Add a source data file transfer for loading into the target table

Transfer the source file to be loaded:
* sql.executeStatement
* fho.tranferFile
* cli.executeCommand

### Add a check for the data loaded

Check if the data has been loaded correctly from the source file:
* sql.executeStatement
* fho.tranferFile
* cli.executeCommand
* sql.evaluateResult

## Integration with technical ETL frameworks

Use case: Check if any file is still queued for processing. This is to avoid to that clashes in processing occur:
* sql.evaluateResult

Transfer the source data file for processing:
* fho.tranferFile

Wait for the file to be picked up by the technical ETL framework:
* Make use of the wfa.executeQueryPing to wait until a simple SQL statement returns a result
* The returned values can be stored as runtime variables for uses in later actions

If the file has been picked up, the ETL flow can be started:
* cli.executeCommand

## Run end-to-end chains of ETL flows

Run multiple ETL flows one after the other across the different repositories:
* Use separate scripts for the different ETL flows
* Each script will contain the necessary steps for the ETL flow execution

## Creating reusable scripts for testing multiple ETL flows

Reuse a common script for testing multiple ETL flows:
* Design the common script
* Make use of runtime variables
  * File transfer: make use of a variable for the file directory and name
  * ETL flow execution: use a variable for the project and job name
  * Cleaning and checking tables: parameterize the schema and table name

*Note: Additional parameters can be applicable depending on the script design*

### Execute using a parameter file

* Create a parameter file per ETL flow

```
ETL1.conf
---------
file=source.csv
path=/sourcePath
project=DStage1
Job=ETL1
Etc.
```

* Run the script using the `paramfile` option

```bash
bin/iesi-launch.sh -script commonScript1 -env <arg> -paramfile /path/ETL1.conf
```

* For every ETL flow (existing or new) add a new configuration file and run the test

### Organize the configuration files using a predefined structure

* Another strategy is to define the script and use a runtime variable to reference a specific folder structure having specific sql, shell, configuration, ... files inside

```
Folder structure
----------------
base
  * ETL1
    * Data
	  * clean.sql
	  * run.sh
	  * checkload.sql
  * ETL2
    * Data
	  * clean.sql
	  * run.sh
	  * checkload.sql
```

* Run the script using the `paramlist` option

```bash
bin/iesi-launch.sh -script commonScript1 -env <arg> -paramlist ETL=ETL1
```

* For every ETL flow (existing or new) add a new folder is added to the structure and the necessary test files are adjusted where needed
