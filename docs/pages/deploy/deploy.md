{% include navigation.html %}

# Deploy the framework

The most simple mode of deployment is a local installation on a server or desktop (N*X / Windows)
* As a next step, the containerization and/or automated deployment in a virtual infrastructure / cloud can be done possible due to limited installation steps

![deploy-basic](/{{site.repository}}/images/deploy/deploy-basic.png)

Next, the configuration can stored in a database, either on the same server or in a central location. 
* Thus, additional instances can be created and use the same design configuration, centralizing the logging
* Additionally, all run time statistics can be send to specialized logging and monitoring solutions (for instance Elasticsearch)
* Data which needs to be used can be kept close to the processing or runtime (which is usually the best way to get started). 
It can however be stored in any central location and accessed accordingly (e.g. NAS, Repository, Version Control System, etc.)

![deploy-advanced](/{{site.repository}}/images/deploy/deploy-advanced.png)

# Scaling

The framework is designed to be distributed by nature and integrate with different types of repositories 
(configuration, data, configuration, results, trace, reporting)

The solution is decoupling configuration – data – execution – results which makes it possible 
to scale and parallelize in different scenarios and adjust them quickly depending on actual needs:
* Execution engines can be added to immediately use centralized metadata 
-> use of virtual / cloud instances grow in steps and segregate between environments / departments / etc.
* The solution can be deployed easily for any size of project without dependencies to any centralized solution 
-> there is no dependency on any complex installation
* Results can be logged in a central logging solution -> always have a centralized view regardless of the deployment mode

![scaling](/{{site.repository}}/images/deploy/scaling.png)

# Installation

* The solution is built packaged for deployment in a compressed file (`.tar` or `.zip`)
* This package can be downloaded or made available in any artifact repository

## Installation steps
* Download and decompress the package in the target location
* Apply the configuration for the solution in the `#iesi.home#/conf` folder
* If the metadata is stored outside the instance, then the solution is ready to be used
* For the first installation, the metadata repository needs to be created: `#iesi.home#/bin/iesi-metadata.sh -type general -create`

If the metadata is needed (partially) inside the instance, metadata actions can be initiated to load necessary metadata: 
first get the necessary metadata file and then run `#iesi.home#/bin/iesi-metadata.sh -type general -load`

In order to automate the installation steps, the configuration can be stored in an artifact or configuration repository and copied to the appropriate location

For new versions and patches, new packages will be made available that can be installed in a similar manner. 
Detailed instructions will be made available to deal with any impact.

### Configure the framework

The framework makes use of different setting files to manage its operations. All setting files are centralized in the `conf` folder. 
Depending on the deployment manner specific settings need to be defined. 
More information on the settings file can be found [here](/{{site.repository}}/pages/manage/manage.html).

# Examples

## Local install

## Configuration stored in a database

## Storing configurations in different logical instances

## Separating connectivity configuration