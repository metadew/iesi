{% include navigation.html %}

# Tutorial: Create first connection

This page guide you through creating your first connection and configuring an action to make use of it. 
We will make use of a database connection to a simple synthetic dataset. 
In that way it will be possible to configure an sql type action.

In this tutorial we will create a script `helloconnection` that will execute a sql type action.

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.
* Download the simplesyntheticdataset.db3 from [https://github.com/metadew/iesi/tree/master/examples/tutorials/data](https://github.com/metadew/iesi/tree/master/examples/tutorials/data)

## Create connection

First, we will create the configuration of the connection:
* We have provided a sample dataset for this tutorial: simplesyntheticdataset.db3 that can be used. 
First copy the file to a location on your system. It is advised to create a work area for your tutorial data. 
We suggest creating a `tutorials` folder inside the `data` folder.
* Open a text editor and create a new file `SimpleSyntheticDatasetConnection.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: Connection
data:
  name: "simplesyntheticdataset"
  type: "db.sqlite"
  description: "Simple Synthetic Dataset for tutorials"
  environment: "tutorial"
  parameters:
  - name: "filePath"
    value: "#iesi.home#/data/tutorials"
  - name: "fileName"
    value: "simplesyntheticdataset.db3"
```

* You will notice that the setting `#iesi.home#` is used to point to the root of the framework folder structure. More information will follow in other tutorials. 
* We advise to use `/` for all path definitions, also for Windows systems.

Next, load the configuration into the configuration repository. You should be getting used to this by now. 
You can always refresh this by having a look the following tutorial: [Load configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html).

## Create script

Now we can create the configuration file for the script to make use of the connection:
* Open a text editor and create a new file `HelloConnectionScript.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: Script
data:
  type: "script"
  name: "helloconnection"
  description: "Hello Connection Tutorial Script"
  actions:
  - number: 1
    type: "sql.evaluateResult"
    name: "VerifyData"
    description: "Verify if Table1 contains data"
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value: "select * from Table1"
    - name: "hasResult"
      value: "Y"
    - name: "connection"
      value: "simplesyntheticdataset"
```

Have a look at the action that we have defined in the above script:
* We are going to verify if an sql statement returns a result or not. The relevant action type for this is *sql.evaluateResult*.
* This action makes use of 3 parameters:
  * The sql query to use. Here we use a basic select all query.
  * The indicator if we expect a result or not. We set the parameter hasResult to `Y` since we expect the table to contain records.
  * The connection where the sql query needs to be executed on. For this, we make use of the `simplesyntheticdataset` connection we just created. 
  You see that we just make use of the connection name, the framework does the rest.

Now, load the configuration into the configuration repository.

## Execute the script

You should be getting used to executing scripts by now. 
You can always refresh this by having a look the following tutorial: [Execute script](/{{site.repository}}/pages/tutorial/executescript.html).

Linux/Mac
```bash
./iesi-launch.sh -script helloconnection -env tutorial
```
Windows
```bash
./iesi-launch.cmd -script helloconnection -env tutorial
```

The following type output will appear on the screen:

```
Option -script (script) value = helloconnection
Option -version (version) value = 
Option -env (environment) value = tutorial
Option -paramlist (parameter list) value = 
Option -paramfile (parameter file) value = 
Option -actions (actions) value = 
Option -settings (settings) value = 
Option -impersonation (impersonation) value = 
Option -impersonate (impersonate) value = 

script.launcher.start
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
2019-04-03 10:47:40,642 INFO  [iesi] - option.script=helloconnection
2019-04-03 10:47:40,643 INFO  [iesi] - option.version=-1
2019-04-03 10:47:40,643 INFO  [iesi] - option.file=
2019-04-03 10:47:40,644 INFO  [iesi] - option.env=tutorial
2019-04-03 10:47:40,644 INFO  [iesi] - option.paramlist=
2019-04-03 10:47:40,644 INFO  [iesi] - option.paramfile=
2019-04-03 10:47:40,644 INFO  [iesi] - option.actionselect=
2019-04-03 10:47:40,644 INFO  [iesi] - option.settings=
2019-04-03 10:47:40,646 INFO  [iesi] - option.impersonation=
2019-04-03 10:47:40,646 INFO  [iesi] - option.impersonate=
2019-04-03 10:47:40,843 INFO  [iesi] - exec.runid=7997a45b-7a09-4523-8ebb-ea348044d9b5
2019-04-03 10:47:41,049 INFO  [iesi] - script.name=helloconnection
2019-04-03 10:47:41,049 INFO  [iesi] - exec.env=tutorial
2019-04-03 10:47:41,068 INFO  [iesi] - action.name=VerifyData
2019-04-03 10:47:41,445 INFO  [iesi] - action.status=SUCCESS
2019-04-03 10:47:41,447 INFO  [iesi] - script.status=SUCCESS
2019-04-03 10:47:41,448 INFO  [iesi] - script.output=
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
script.launcher.end
```

The outcome of the script is a success, there is data in Table1. 

You might want to start exploring alternative checks with this dataset. 
When you update the script setting `hasResult=N`: This will result in an error; there is data but you are not expecting it.

```
Option -script (script) value = helloconnection
Option -version (version) value = 
Option -env (environment) value = tutorial
Option -paramlist (parameter list) value = 
Option -paramfile (parameter file) value = 
Option -actions (actions) value = 
Option -settings (settings) value = 
Option -impersonation (impersonation) value = 
Option -impersonate (impersonate) value = 

script.launcher.start
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
2019-04-03 10:50:41,832 INFO  [iesi] - option.script=helloconnection
2019-04-03 10:50:41,834 INFO  [iesi] - option.version=-1
2019-04-03 10:50:41,834 INFO  [iesi] - option.file=
2019-04-03 10:50:41,834 INFO  [iesi] - option.env=tutorial
2019-04-03 10:50:41,834 INFO  [iesi] - option.paramlist=
2019-04-03 10:50:41,834 INFO  [iesi] - option.paramfile=
2019-04-03 10:50:41,834 INFO  [iesi] - option.actionselect=
2019-04-03 10:50:41,834 INFO  [iesi] - option.settings=
2019-04-03 10:50:41,840 INFO  [iesi] - option.impersonation=
2019-04-03 10:50:41,840 INFO  [iesi] - option.impersonate=
2019-04-03 10:50:42,031 INFO  [iesi] - exec.runid=33a88ee7-71fb-45c3-ad66-6dadc21f2818
2019-04-03 10:50:42,224 INFO  [iesi] - script.name=helloconnection
2019-04-03 10:50:42,225 INFO  [iesi] - exec.env=tutorial
2019-04-03 10:50:42,245 INFO  [iesi] - action.name=VerifyData
2019-04-03 10:50:42,626 INFO  [iesi] - action.status=ERROR
2019-04-03 10:50:42,628 INFO  [iesi] - script.status=ERROR
2019-04-03 10:50:42,629 INFO  [iesi] - script.output=
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
script.launcher.end
```

## Recap

We have now created a script that makes use of a connection and executed it on the tutorial environment. 
We can now start creating other connections to be used with the available action types.
