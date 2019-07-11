{% include navigation.html %}

# Tutorial: Define the environment where to execute a script

This page guide you through defining the environment where to execute a script. 
An environment is a group of connectivity end points that belong together. 
Systems will be set up to communicate and work with each other. 
In order to implement functionalities and verify that these are working fine, new instances are created. 
The environment concept in the automation framework allows to make and abstraction of these instances and identify on which one an script needs to be executed. 

When a script is executed, the environment on which the script needs to be executed is mandatory.

## Pre-requisites

none

## Command line

When executing a script from the command line via the *script launcher* the environment is passed as an argument
* the environment argument is mandatory
* this is the name of the environment

```bash
bin/iesi-launch.sh -script <arg> -env <arg>
```
