{% include navigation.html %}

# Tutorial: Create environment

This page guide you through creating the tutorial environment. 
An environment is a group of connectivity end points that belong together. 
Systems will be set up to communicate and work with each other. 
In order to implement functionalities and verify that these are working fine, new instances are created. 
The environment concept in the automation framework allows to abstract these instances and to identify which one a particular script needs to be executed on.
The environment that will be set up for the different tutorials will be named `tutorial`.

## Pre-requisites

* The framework has been installed. See the [quickstart](/{{site.repository}}/pages/quickstart.html) guide for more information.

## Create configuration

First, we will create the configuration file for the tutorial environment:
* Open a text editor and create a new file `TutorialEnvironment.yml`
* Edit the configuration file and add the configuration for this environment:

```yaml
---
type: Environment
data:
  name: "tutorial"
  description: "Tutorial environment"
```

## Load configuration

Next, we will load the tutorial environment configuration file into the configuration repository:
* Copy the configuration file to the `metadata/in/new` folder
* Navigate to the `bin` folder and open the terminal (or command prompt on windows) in this folder
* Load the configuration file via the `./iesi-metadata.sh` (or `iesi-metadata.cmd` on Windows) command [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/operate/operate.html)

Linux/Mac
```bash
./iesi-metadata.sh -load -type general
```
Windows
```bash
./iesi-metadata.cmd -load -type general
```

## Recap

We have now created an environment for our tutorials named `tutorial`. 
When we will execute scripts we can make reference to this environment name.

Linux/Mac
```bash
./iesi-launch.sh -script <arg> -env tutorial
```
Windows
```bash
./iesi-launch.cmd -script <arg> -env tutorial
```

## Continue?

Do you want to continue immediately? Move forward to: [Create Hello World script](/{{site.repository}}/pages/tutorial/helloworldscript.html)
