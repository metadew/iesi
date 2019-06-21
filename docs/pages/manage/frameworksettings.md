{% include navigation.html %}

# Framework settings

The framework settings file defines the necessary values for the functions that a framework administrator can set during deployment and operations.
* The settings file syntax is `keyvalue`
* The settings file is required to be loaded in order for the framework to work correctly

## Solution

### 1: iesi.identifier

`iesi.identifier=iesi`
* define the identifier for the framework
* the setting is used as prefix for a number of settings and operations
* it is advised to *not* modify this setting, it requires advanced knowledge of its impacts

### 2: iesi.host.name

`iesi.host.name=<host>`
* define the host name where the framework is running on
* optional setting (placeholder for future enhancements)

## Runtime

### 1: iesi.script.execution.runtime

`iesi.script.execution.runtime=<classpath>`
* define a customized execution runtime for scripts
* this option allows to link a customization of the execution runtime without modifying the framework. This option is commonly used for specific organization plugins that are connected to the framework. 

## Display options

### 1: iesi.script.execution.runtime

`iesi.commandline.display.runtime.variable=<boolean>`
* define if the resolution of variables needs to be displayed on the commandline when running a script
* options are: `Y` or `N`

## Security

### 1: iesi.guard.authenticate

`iesi.guard.authenticate=<boolean>`
* define if the framework requires authentication when running scripts
* options are: `Y` or `N`

**Work in progress**

## Server

### 1: iesi.server.mode

`iesi.server.mode=<mode>`
* Set the framework server mode
* options are:
  * `off`: use the framework without server, execute entirely from commandline
  * `standalone`: use the framework using a standalone server instance (no managed workload distribution)

**Work in progress**
