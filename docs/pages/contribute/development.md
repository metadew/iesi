{% include navigation.html %}

# How to setup a development machine

## Install development software

First, install the necessary development software on your machine:
* IntelliJ > [link](https://www.jetbrains.com/idea)
* IntelliJ plugins: Lomkok > [link](https://plugins.jetbrains.com/plugin/6317-lombok)
* Hub - command line tool for git > [link](https://github.com/github/hub)

Optionally, install any additional tools that facilitate your work (text editors, ...)

## Get the git repository

Next, clone the git repository for [iesi](https://github.com/metadew/iesi).

## Import external maven dependencies

There are some external libraries used that are not part of maven central. These need to be imported into your local maven maven repository before continuing. From the shell inside IntelliJ, perform the instructions listed in the following [page](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

This process needs to be performed for all jar files in the folder build/ext.

## Build the iesi java projects

Next, open each of the following java projects in IntelliJ and organise them according to your favourite way of working.
* core/java/iesi-core
* core/java/iesi-test
* core/java/iesi-rest-without-microservices

In the order of the projects listed above, each one will be built.

### iesi-core

Build using maven goal `clean install project-info-reports:dependencies` and include the profile `dependencies`

### iesi-test

Build using maven goal `clean install`

### iesi-rest-without-microservices

Build using maven goal `clean package project-info-reports:dependencies`

## Create workspace

Now that each project is built, the solution can be assembled in a workspace:
* create a workspace folder for sandboxing. This folder needs to reside outside of the iesi folder structure
* add a `conf` folder inside the workspace folder for storing all necessary configuration

## Assemble the solution

Once the workspace folder has been created, the solution can be assembled. Each assembly has a `version` that will be pulled togehter into an `instance` and for wich a `configuration` will be applied.
* When starting an assembly, the solution will be deployed to `[workspace]/[instance]/[version]`
* The configuration that is available in `[workspace]/conf/[instance]/[configuration]`

So to move forwasrd, you need to create a folder fo the instance and configuration in the `[workspace]/conf` folder.

Next, the assembly process can be started from iesi-core java project. Start the AssemblyLauncher - `io.metadew.iesi.launch.AssemblyLauncher` - with the following program arguments:
```
-repository [/path/to/iesi] 
-development [/path/to/iesi] 
-sandbox [/path/to/workspace] 
-instance [instance] 
-version [version] 
-configuration [configuration] 
-distribution
```

After completion, the solution will be deployed to `[workspace]/[instance]/[version]`