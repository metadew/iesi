{% include navigation.html %}

# Tutorial: Hello World

This page guide you through creating your first hello world script. 
A script is a a set of actions that will be executed automatically by the framework. 
It is designed independently of the environment where it will be executed. 
At runtime, the framework will make sure that the script executes on the selected environment.
Therefore, every time a script is executed two parameters need to be provided: the script name and the environment name where the script needs to be executed on.

In this tutorial we will create a script `helloworld` that will execute a dummy action.

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.

## Create configuration

First, we will create the configuration file for the script:
* Open a text editor and create a new file `HelloWorldScript.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: Script
data:
  type: "script"
  name: "helloworld"
  description: "Hello World Tutorial Script"
  actions:
  - number: 1
    type: "fwk.dummy"
    name: "HelloWorld"
    description: "HelloWorld Action"
    errorExpected: "N"
    errorStop: "N"
    parameters: []
```

## Load configuration

Next, we will load the configuration file into the configuration repository:
* Copy the configuration file to the `metadata/in/new` folder
* Navigate to the `bin` folder and open the terminal (or command prompt on Windows) in this folder
* Load the configuration file via the `./iesi-metadata.sh` (or `iesi-metadata.cmd` on Windows) command [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/operate/operate.html)

Linux/Mac
```bash
./iesi-metadata.sh -load -type general
```
Windows
```bash
./iesi-metadata.cmd -load -type general
```

This allows you to store/load your created 'HelloWorld'-script in the configured central repository. You will now be able to execute your script with the next command(s).

## Execute the script

Finally, we will execute the script:
* Navigate to the `bin` folder and open the terminal (or command prompt on Windows) in this folder
* Execute the script via the `./iesi-launch.sh` (or `./iesi-launch.cmd` on Windows) command providing 
the `script` and `environment` option: execute a script on a given environment. 

Linux/Mac
```bash
./iesi-launch.sh -script helloworld -env tutorial
```
Windows
```bash
./iesi-launch.cmd -script helloworld -env tutorial
```

The following type output will appear on the screen:

```
Option -script (script) value = helloworld
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
2019-04-01 08:25:14,057 INFO  [iesi] - option.script=helloworld
2019-04-01 08:25:14,059 INFO  [iesi] - option.version=-1
2019-04-01 08:25:14,059 INFO  [iesi] - option.file=
2019-04-01 08:25:14,059 INFO  [iesi] - option.env=tutorial
2019-04-01 08:25:14,059 INFO  [iesi] - option.paramlist=
2019-04-01 08:25:14,059 INFO  [iesi] - option.paramfile=
2019-04-01 08:25:14,059 INFO  [iesi] - option.actionselect=
2019-04-01 08:25:14,059 INFO  [iesi] - option.settings=
2019-04-01 08:25:14,059 INFO  [iesi] - option.impersonation=
2019-04-01 08:25:14,065 INFO  [iesi] - option.impersonate=
2019-04-01 08:25:14,267 INFO  [iesi] - exec.runid=e229be7a-914f-4b3f-a598-b635ef7b5c28
2019-04-01 08:25:14,494 INFO  [iesi] - script.name=helloworld
2019-04-01 08:25:14,494 INFO  [iesi] - exec.env=tutorial
2019-04-01 08:25:14,514 INFO  [iesi] - action.name=HelloWorld
2019-04-01 08:25:14,833 INFO  [iesi] - action.status=SUCCESS
2019-04-01 08:25:14,837 INFO  [iesi] - script.status=SUCCESS
2019-04-01 08:25:14,838 INFO  [iesi] - script.output=
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
script.launcher.end
```

## Recap

We have now created a Hello World script and executed it on the tutorial environment. 
We can now start building scripts with additional functionalities. 

## Continue?

Do you want to continue immediately? Move forward to:
* [Working with script versions](/{{site.repository}}/pages/tutorial/workingwithscriptversions.html)
* [Create first connection](/{{site.repository}}/pages/tutorial/createfirstconnection.html)
