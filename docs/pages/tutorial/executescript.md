{% include navigation.html %}

# Tutorial: Hello World

This page guide you through executing scripts that have been loaded into the configuration repository. 

## Pre-requisites

* The framework has been installed. See the [quickstart](/{{site.repository}}/pages/quickstart.html) guide for more information.
* The appropriate connectivity configuration has been loaded: environments and connections. 
  * For environments, see the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html)
  * For connections, see the tutorial [create first connection](/{{site.repository}}/pages/tutorial/createfirstconnection.html)

## Execute the script

Finally, we will execute the script:
* Navigate to the `bin` folder
* Execute the script via the `bin/iesi-launch.sh` (or `bin/iesi-launch.cmd` on Windows) command providing 
the `script` and `environment` option: execute a script on a given environment. 

```bash
bin/iesi-launch.sh -script <arg> -env <arg>
```

## Recap

We have now executed a script that was previously loaded into the configuration repository. 