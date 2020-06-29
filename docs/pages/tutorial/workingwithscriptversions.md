{% include navigation.html %}

# Tutorial: Working with script versions

This page guide you through creating your first script having two versions. 
A script will be created to test the functionality of a component. 
When this component changes in a next version, the script will need to be updated. 
However, you will still want to test both versions of the component with the script. 
This is why we have introduced the version concept.

A script can have many versions. When you start a script, you can select the version that needs to be executed. 
By default, the latest version will be selected.

In this tutorial we will create a script `helloworldversion` that will execute a dummy action. 
In the second version of the script, we will add an additional dummy action.

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.

## Create configuration

First, we will create the configuration file for the script's first version:
* Open a text editor and create a new file `HelloWorldVersionScript1.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: Script
data:
  type: "script"
  name: "helloworldversion"
  description: "Hello World Tutorial Script"
  version:
    number: 1
    description: "Version 1"
  actions:
  - number: 1
    type: "fwk.dummy"
    name: "HelloWorld"
    description: "HelloWorld Action"
    errorExpected: "N"
    errorStop: "N"
    parameters: []
```

Next, we will create the configuration file for the script's second version:
* Open a text editor and create a new file `HelloWorldVersionScript2.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: Script
data:
  type: "script"
  name: "helloworldversion"
  description: "Hello World Tutorial Script"
  version:
    number: 2
    description: "Version 2"
  actions:
  - number: 1
    type: "fwk.dummy"
    name: "HelloWorld"
    description: "HelloWorld Action"
    errorExpected: "N"
    errorStop: "N"
    parameters: []
  - number: 2
    type: "fwk.dummy"
    name: "HelloWorld2"
    description: "Additional HelloWorld Action"
    errorExpected: "N"
    errorStop: "N"
    parameters: []
```

In this configuration, you will notice that
* The version number has been increased
* A second action has been added

## Load configuration

Next, we will load the configuration files into the configuration repository:
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

## Execute the scripts

Finally, we will execute the scripts:
* Navigate to the `bin` folder and open the terminal (or command prompt on Windows) in this folder
* Execute the script via the `./iesi-launch.sh` (or `./iesi-launch.cmd` on Windows) command providing 
the `script` and `environment` option: execute a script on a given environment. 

Linux/Mac
```bash
./iesi-launch.sh -script helloworldversion -env tutorial -version 1
```
Windows
```bash
./iesi-launch.cmd -script helloworldversion -env tutorial -version 1
```

The following type output will appear on the screen. Note that version 1 is selected.

```
Option -script (script) value = helloworldversion
Option -version (version) value = 1
Option -env (environment) value = tutorial
Option -paramlist (parameter list) value = 
Option -paramfile (parameter file) value = 
Option -actions (actions) value = 
Option -settings (settings) value = 
Option -impersonation (impersonation) value = 
Option -impersonate (impersonate) value = 

script.launcher.start
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
2019-04-02 19:48:58,210 INFO  [iesi] - option.script=helloworldversion
2019-04-02 19:48:58,211 INFO  [iesi] - option.version=1
2019-04-02 19:48:58,211 INFO  [iesi] - option.file=
2019-04-02 19:48:58,211 INFO  [iesi] - option.env=tutorial
2019-04-02 19:48:58,211 INFO  [iesi] - option.paramlist=
2019-04-02 19:48:58,212 INFO  [iesi] - option.paramfile=
2019-04-02 19:48:58,212 INFO  [iesi] - option.actionselect=
2019-04-02 19:48:58,212 INFO  [iesi] - option.settings=
2019-04-02 19:48:58,250 INFO  [iesi] - option.impersonation=
2019-04-02 19:48:58,250 INFO  [iesi] - option.impersonate=
2019-04-02 19:48:58,444 INFO  [iesi] - exec.runid=6aa6f8bd-3b43-4130-896f-212fea680ce9
2019-04-02 19:48:58,665 INFO  [iesi] - script.name=helloworldversion
2019-04-02 19:48:58,665 INFO  [iesi] - exec.env=tutorial
2019-04-02 19:48:58,684 INFO  [iesi] - action.name=HelloWorld
2019-04-02 19:48:59,003 INFO  [iesi] - action.status=SUCCESS
2019-04-02 19:48:59,006 INFO  [iesi] - script.status=SUCCESS
2019-04-02 19:48:59,008 INFO  [iesi] - script.output=
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
script.launcher.end
```

Now, the script's second version will be executed.

Linux/Mac
```bash
./iesi-launch.sh -script helloworldversion -env tutorial -version 2
```
Windows
```bash
./iesi-launch.cmd -script helloworldversion -env tutorial -version 2
```

The following type output will appear on the screen. Note that version 2 is selected. The additional action is now also executed.

```
Option -script (script) value = helloworldversion
Option -version (version) value = 2
Option -env (environment) value = tutorial
Option -paramlist (parameter list) value = 
Option -paramfile (parameter file) value = 
Option -actions (actions) value = 
Option -settings (settings) value = 
Option -impersonation (impersonation) value = 
Option -impersonate (impersonate) value = 

script.launcher.start
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
2019-04-02 19:50:52,414 INFO  [iesi] - option.script=helloworldversion
2019-04-02 19:50:52,416 INFO  [iesi] - option.version=2
2019-04-02 19:50:52,416 INFO  [iesi] - option.file=
2019-04-02 19:50:52,416 INFO  [iesi] - option.env=tutorial
2019-04-02 19:50:52,416 INFO  [iesi] - option.paramlist=
2019-04-02 19:50:52,416 INFO  [iesi] - option.paramfile=
2019-04-02 19:50:52,416 INFO  [iesi] - option.actionselect=
2019-04-02 19:50:52,416 INFO  [iesi] - option.settings=
2019-04-02 19:50:52,423 INFO  [iesi] - option.impersonation=
2019-04-02 19:50:52,423 INFO  [iesi] - option.impersonate=
2019-04-02 19:50:52,604 INFO  [iesi] - exec.runid=e291374a-d144-47de-a2d2-5476ad2b6067
2019-04-02 19:50:52,807 INFO  [iesi] - script.name=helloworldversion
2019-04-02 19:50:52,807 INFO  [iesi] - exec.env=tutorial
2019-04-02 19:50:52,826 INFO  [iesi] - action.name=HelloWorld
2019-04-02 19:50:53,084 INFO  [iesi] - action.status=SUCCESS
2019-04-02 19:50:53,086 INFO  [iesi] - action.name=HelloWorld2
2019-04-02 19:50:53,239 INFO  [iesi] - action.status=SUCCESS
2019-04-02 19:50:53,246 INFO  [iesi] - script.status=SUCCESS
2019-04-02 19:50:53,248 INFO  [iesi] - script.output=
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
script.launcher.end
```

If you will execute script without specifying a version, the latest version will be selected by default.

Linux/Mac
```bash
./iesi-launch.sh -script helloworldversion -env tutorial
```
Windows
```bash
./iesi-launch.cmd -script helloworldversion -env tutorial
```

## Recap

We have now created a Hello World script that has two different versions. 
We have executed both versions independently on the tutorial environment. 
We can now start building scripts for different versions of the same component. 

