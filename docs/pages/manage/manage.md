{% include navigation.html %}

# Manage the framework

The framework makes use of different setting file to manage its configuration. All setting files are centralized in the `conf` folder

## Basic concepts

The framework loads its setting files with every execution according to the file `conf/iesi-conf.ini`. This ini file defines:
* the setting file location - this needs to be expressed with the absolute path
* the order of loading - this is the sequence of files
* the type of setting file - options are: keyvalue (`key=value`) or linux (`export key=value`)

Important:
* to dynamically refer to the file location with a dynamic path, the variable `#iesi.home#` can be used
* settings that have been loaded in a previous file can be used as variables (not defined in the same file)
* when setting values are loaded multipe times, only the last value loaded will be used

> This construct allows the creation of common settings that can be defined for all users. 
> If relevant, a contextual meaning can be introduced as well (environment, release, etc.). 
> Make sure to avoid impact on the overall settings by loading them first.

