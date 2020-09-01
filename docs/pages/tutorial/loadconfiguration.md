{% include navigation.html %}

# Tutorial: Load configuration

This page guide you through loading your configuration into the configuration repository.

## Pre-requisites

* The framework has been installed. See the [quickstart](/{{site.repository}}/pages/quickstart.html) guide for more information.

## Load configuration

We will load the configuration file into the configuration repository as follows:
* Copy the configuration file to the `metadata/in/new` folder
* Navigate to the `bin` folder and open the terminal (or command prompt on Windows) in this folder
* Load the configuration file via the `./iesi-metadata.sh` (or `./iesi-metadata.cmd` on Windows) command [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/operate/operate.html)

Linux/Mac
```bash
./iesi-metadata.sh -load -type general
```
Windows
```bash
./iesi-metadata.cmd -load -type general
```

This process is universal for all configuration types: environments, connections, scripts, etc.

```
Option -type (type) value = general
metadata.launcher.start

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Option -load (load) selected

2019-04-03 10:02:22,893 INFO  [iesi] - metadata.load.start
2019-04-03 10:02:22,905 INFO  [iesi] - metadata.file=<file.ext>
2019-04-03 10:02:23,158 INFO  [iesi] - metadata.load.end
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

metadata.launcher.end
```

## Recap

We have now loaded configuration files into the configuration repository. 
This configuration can now be used by the framework. 
