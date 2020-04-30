{% include navigation.html %}

# Quick start

This page provides a quick start getting up and running with IESI. 
It will install the automation framework for first use getting you started. 

**Important**

Also have a look at these [important highlights](/{{site.repository}}/pages/understand/highlights.html)

# Installation

## Download package

First, download the latest package of our framework. 
For each release the packages are provided as a zip or tar archive. 
More details can be found [here](/{{site.repository}}/pages/download.html).

## Install package

**Pre-requisites**

* The framework requires at least Java 8. It is tested against build 1.8.0_181-b13 but has previously run against many builds. 
* The java command is available on the path and can be executed without static path. 

**Uncompress package**

As a next step, the archive can be uncompressed to any folder. 
You might want to consider creating a dedicated area on your machine. 
It will then create all required files to run the solution. 
But first, some configuration steps need to be completed. 

## Configure for first use 
The solution uses a repository to manage configuration and results. 

First, open `conf/application-repository.yml` and complete the necessary configuration. 
While different types of repository can be used, we will use the most basic option, a local SQLite database. 
```yaml
iesi:
  metadata:
    repository:
      - categories:
          - general
        coordinator:
          type: sqlite
          file: c:/path.to.your.database/database.db3
```

Next, the configuration repository will need to be created. Open a command window or shell: 
* navigate to the `bin` folder
* all configuration management is via the `bin/iesi-metadata.sh` (or `iesi-metadata.cmd` on Windows) command [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/operate/operate.html)

```bash
bin/iesi-metadata.sh -create -type general
```

> * drop ensures that even if the tables exist they are removed first 
> * create ensures that all necessary tables are created 
> * load will load any data that is made available in the folder metadata/in/new 
> * type is related to the type of metadata repository

## Verify first execution 

You can verify the first execution of the framework by completing the following tutorials:
* [Create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html)
* [Create Hello World script](/{{site.repository}}/pages/tutorial/helloworldscript.html)

# Getting started with automation

The automation framework is designed around the principle to configure scripts once and execute them many times. 
The system landscape is defined upfront so that it can be referred to while designing scripts.

The process of automating consists of four steps:
* Define the system landscape
* Configure the script
* Execute the script
* Verify the execution results

## Defining the system Landscape

Before the process of automating can begin the necessary definitions need to be configured in the framework. 
This will be a periodic activity that needs to be performed at the beginning and whenever a relevant change occurs in the landscape occurs. 
The design and execution processes will make use of the defined logical names that need to be made available to relevant users.

The two configurations that are crucial to get started are the environments and the connections:
* The environment configuration defines the number of environments (development, test, ..., production)
* The connection configuration defines the different systems that the framework can connect to, including all necessary details such as physical address, user name, password, etc.

## Configure the script

## Execute the script

Basic execution of a script is done via the `bin/iesi-launch.sh` (or `bin/iesi-launch.cmd` on Windows) command providing 
the `script` and `environment` option: execute a script on a given environment. 

```bash
bin/iesi-launch.sh -script <arg> -env <arg>
```

## Verify the execution results

We are working providing more details on this.
