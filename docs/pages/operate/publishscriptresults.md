{% include navigation.html %}

# Publish script results

The framework allows to publish the script result externally at the end of the execution. From this publication, external process can pick up the information and process it further.

## Google Cloud Pubsub


  publishers:
    - type: gcppubsub
      project:
      topic:
      

Google credentials
export GOOGLE_APPLICATION_CREDENTIALS="service-account.json"
 

The configuration stored in the configuration repository is managed via the `bin/iesi-metadata.sh` (or `bin/iesi-metadata.cmd` on Windows) command.

The following options are available for the command:

|Option|Description|
|------|-----------|
|-clean|clean all tables in the metadata repository|
|-create|create all metadata tables in the metadata repository|
|-drop|drop all metadata tables in the metadata repository|
|-exit|define if an explicit exit is required|
|-files|filename(s) to load from the input folder into the metadata repository|
|-help|displays the help message|
|-load|load metadata file from the input folder into the metadata repository|
|-type|define the type of metadata repository|

**Tip**
* It is possible to combine several actions in a single command according to the following order: drop, create, clean, load

### Loading configuration

All configuration items of the framework are structured using the json format.
The different templates help to generate the correct structure easily.
In this way, an item can be loaded into the configuration repository through the `metadata/in/new` folder.
* Place the configuration json file in the `metadata/in/new` folder
* Execute the `bin/iesi-metadata.sh` (or `bin/iesi-metadata.cmd` on Windows) command with following options:
  * -load
  * (optional) -files [expression]
  
**File expression options**
* a single file name including extension
```
Example: Script.json
```
* list of files separated by commas
```
Example: Script1.json,Script2.json
```
* a regular expression written as function
```
=regex([your expression])
Example: =regex(.+\json) > this will load all files
```

## Encrypt Credentials

All credentials need to be encrypted via the `bin/iesi-encrypt.sh` (or `iesi-encrypt.cmd` on Windows) command.
* Encrypted values always apply the following syntax `ENC([EncryptedValue])`

## Execute Script

The automation scripts can be executed via the `bin/iesi-launch.sh` (or `iesi-launch.cmd` on Windows) command.
The appropriate metadata repositories as defined in the framework's settings will be used to retrieve the configuration and store the execution results.

The following options are available for the command:

|Option|Description|
|------|-----------|
|-env <arg>|define the environment name where the execution needs to take place|
|-exit|define if an explicit exit is required|
|-file <arg>|define the file to execute|
|-help|displays the help message|
|-impersonation <arg>|define impersonation name to use|
|-paramlist <arg>|define a list of parameters to use|
|-script <arg>|define the script name to execute|
|-version <arg>|define the version of the script to execute|
|-labels <arg>|define the labels that are attached to the script execution run|

### Basic Execution

The most basic method to execute a script is to use the `script` and `environment` option: execute a script on a given environment.
The environment where an execution takes place is always required.

```bash
bin/iesi-launch.sh -script <arg> -env <arg>
```

### Adding User-Defined Parameters

It is possible to add one or more user-defined parameters from the commandline when starting the script execution. The `paramlist` option allows to specify parameters and values directly on the commandline
```
-paramlist var1=value1,var2=value2
```

## Defining if an explicit exit is required

It is possible to define explicitely if a process needs to exit.
This is useful when running a *server* or *automated* type approach launching several launches one after the other.
* The value can be `true` of `y` for confirming to exit explicitely
* The value can be `false` or `n` for confirming that exit cannot be explicite
* By default the value will be set to `true`

```
-exit [VALUE]
```

Example

```
-exit false
```

## External triggering

* The solution can be executed via command line scripts that are located in `#iesi.home#/bin`
* A separate version is available for N*X and Windows platforms.
* External triggering of the scripts is possible using default methods, examples include:
  * Scheduling using cron
  * Enterprise scheduler integration (e.g. Tivoli, Opswise, UC4, etc.)
  * Triggering via Jenkins
    * via the standard ssh panel in a Jenkins project
    * Via Groovy scripts (recommended for versioning and control – scripts are located in scm)
    * Etc.
  * Triggering via Ansible playbooks
  * Etc.

We are continuously working to improve execution triggering and execution management.
New features in the future will include rest server triggering, internal dependency broker, internal scheduler,
user interface, etc.
