{% include navigation.html %}

# Manage the framework

The framework makes use of different setting files to manage its configuration. All configuration setting files are centralized in the `conf` folder

## Concept

The framework initialization file defines the set of individual configuration setting files that are loaded when starting the framework. 
Each setting file will be responsible for a specifc function performed by the framework. The combination of settings make sure that everything works correctly:
* The initialization file will define the configuration setting files to load
* Each configuration setting file has a type that will define for which function the settings will be applied
* User defined configuration settings can be loaded as well. These will be available as global runtime variables. 
More information can be found [here](/{{site.repository}}/pages/understand/understand.html).

## Initialization file

An initialization file contains a list of configuration setting files. The default file is `conf/iesi-conf.ini`, but this can be overwritten when starting the framework:
* Create a new initialization file and store it in the `conf` folder
* Make use of the `-ini <arg>` option when starting the framework or one of its scripts. The argument to provide is the file name that is stored in the `conf` folder

### Structure

The initialization file contains 1 line for each configuration setting file, each line containing the following information:
* the syntax of the setting file - options are:
  * `keyvalue` (`key=value`)
  * `linux` (`export key=value`)
  * `windows` (`set key=value`)
* the type of setting file - options are:
  * `general`: defines general framework configuration settings
  * `repository`: defines the configuration settings for a configuration repository
* the setting file location - this needs to be expressed with the absolute path

Important:
* The order of loading the setting files is defined by their sequence, top to bottom: the first files is loaded first and so on ...
* To dynamically refer to the file location with a dynamic path, the variable `#iesi.home#` can be used
* Settings that have been loaded in a previous file can be used as variables (not defined in the same file)
* When setting values are loaded multipe times, only the last value loaded will be retained for use

### Example

```
keyvalue,general,#iesi.home#/conf/iesi-default.conf
keyvalue,repository,#iesi.home#/conf/iesi-repository.conf
```

### Mandatory

For the framework to work correctly, the initialization file needs to contain:
* [the default framework configuration setting file](/{{site.repository}}/pages/manage/frameworksettings.html) (`type=general`)
* at least 1 [repository configuration setting file](/{{site.repository}}/pages/manage/repositorysettings.html) (`type=repository`)

### Optional

Optionally, additional configuration setting files can be added allowing the creation of common settings that can be defined for all users. 
If relevant, a contextual meaning can be introduced as well (environment, release, etc.). 

*Make sure to avoid impact on the overall settings by loading them first.*

## Configration setting files

### Syntax

There are three types of syntax that can be used to express configuration settings. Only one syntax type can be used inside a file.
* `keyvalue`: all settings are expressed as `key=value`
* `linux`: all settings are expressed as `export key=value`)
* `windows`: all settings are expressed as `set key=value`

Comments are allowed by prefixing the line with the `#` symbol.

#### Example for keyvalue syntax

```
# Solution
iesi.identifier=iesi
```

#### Example for linux syntax

```
# Solution
export iesi.identifier=iesi
```

#### Example for windows syntax

```
# Solution
set iesi.identifier=iesi
```

### Types

The framework makes use of the following setting file types:
* `general`: defines [general framework settings](/{{site.repository}}/pages/manage/frameworksettings.html)
  * The configuration values are loaded as global runtime variables
  * The values that are defined in the default framework setting file are bound to the appropriate framework function
  * Others are available in the solution via the variable mechanism as described [here](/{{site.repository}}/pages/understand/understand.html)
* `repository`: defines the settings for a [repository](/{{site.repository}}/pages/manage/repositorysettings.html)
  * The configuration values are bound into the appropriate repository category that is defined in the configuration file (general, connectivity, design, ...)
  * The value for the type of repository defines which settings are used. Non-relevant settings are discarded.
