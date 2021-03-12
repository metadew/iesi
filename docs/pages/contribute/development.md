{% include navigation.html %}

# How to setup a development machine

## Install development software

First, install the necessary development software on your machine:
* IntelliJ > [link](https://www.jetbrains.com/idea)
* IntelliJ plugins: Lomkok > [link](https://plugins.jetbrains.com/plugin/6317-lombok). *Note: If the Lombok plugin installation still results in issues, try the following resolution path: uninstall -> Invalidate Caches/Restart -> re-install*

Optionally, install any additional tools that facilitate your work (text editors, ...)

## Get the git repository

Next, clone the git repository for [iesi](https://github.com/metadew/iesi).

## Import external maven dependencies

There are some external libraries used that are not part of maven central. These need to be imported into your local maven repository before continuing. From the shell inside IntelliJ, perform the instructions listed in the following [page](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

This process needs to be performed for all jar files in the folder build/ext.

## Build the iesi java projects

Next, open each of the following java projects in IntelliJ and organise them according to your favourite way of working.
* core/java/iesi-core
* core/java/iesi-rest-without-microservices
* core/java/iesi-test (optional)

In the order of the projects listed above, each one will be built.

### iesi-core

Build using maven goal `clean install project-info-reports:dependencies` and include the profile `dependencies`

### iesi-rest-without-microservices

Build using maven goal `clean install project-info-reports:dependencies`

### iesi-test

Build using maven goal `clean install`

## Create workspace

Now that each project is built, the solution can be assembled in a workspace:
* create a workspace folder for sandboxing. This folder needs to reside outside of the iesi folder structure
* add a `conf` folder inside the workspace folder for storing all necessary configuration

## Assemble the solution

Once the workspace folder has been created, the solution can be assembled. Each assembly has a `version` that will be pulled togehter into an `instance`. When starting an assembly, the solution will be deployed to `[workspace]/[version]/[instance]`

Next, the assembly process can be started from iesi-core java project. Start the AssemblyLauncher - `io.metadew.iesi.launch.AssemblyLauncher` - with the following program arguments:
```
-repository [/path/to/iesi]
-sandbox [/path/to/workspace]
-version [version]
-instance [instance]
```

After completion, the solution will be deployed to `[workspace]/[version]/[instance]`
