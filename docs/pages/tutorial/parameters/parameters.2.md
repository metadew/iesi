{% include navigation.html %}

# Tutorial: Define a parameter value inside a script

This page guide you through creating executing a script while defining a parameter value inside a script. 
We will make use of the action type `setParameterValue`. 
In that way it will be possible to increase the reusability factor of the automation design. 

In this tutorial we will create a script `parameters.2` that will make use of a parameter list that is provided when executing the script.

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.

## Create script

First, we will create the script that makes use of a parameter in its action design. 
Parameters are defined using the `#` symbol before and after its name: `#variable#`. 
you can get more information about this as part of the [design](/{{site.repository}}/pages/design/design.html) page.
* Open a text editor and create a new file `parameters.1.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: "script"
data:
  name: "parameters.2"
  description: "use a parameter defined from the command line using the fwk.setParameterValue action type"
  parameters: []
  actions:
  - number: 1
    type: "fwk.setParameterValue"
    name: "action1"
    description: "set parameter"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "param1"
    - name: "value"
      value : "value1"
  - number: 2
    type: "fwk.outputMessage"
    name: "action2"
    description: "display parameter"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "param1 has value: #param1#"
    - name: "onScreen"
      value : "Y"
```

Have a look at the action that we have defined in the above script:
* We make use of the fwk.outputMessage action that allows us to print any message. 
We set the parameter `onScreen: "Y"` so that we can immediately verify the output in the console after execution. 
* We define a message that is composed of a fixed string combined with our parameter `param1` that we enclose using the `#` symbol.

> Reference file name: parameters.2.yml

## Load and execute the script

Now, [load the configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html) into the configuration repository 
and [execute the script](/{{site.repository}}/pages/tutorial/executescript.html). 

```bash
bin/iesi-launch.sh -script parameters.2 -env tutorial
```

You will notice the following output:

```
2019-04-05 07:39:44,806 INFO  [iesi] - action.message=param1 has value: value1
```

You will notice that no input from the command line is required since it is defined inside the script itself. 
This allows to create scripts that can be updated very quickly avoiding to duplicate values all over the place. 
A typical implementation is to create specific environment or project dependent scripts with a list of values that can be loaded. 
In a later phase, 
* these values can be bound to input parameters
* the variables can be derived from other actions

## Recap

We have now created a script that makes use of a parameter that has been defined inside a script and executed it on the tutorial environment. 
We can now start making use of parameterized actions to create better reusable automation designs.
