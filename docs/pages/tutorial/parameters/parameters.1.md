{% include navigation.html %}

# Tutorial: Execute script using a parameter list

This page guide you through creating executing a script while defining a list of parameters to use at the start. 
We will make use of the option `paramlist` which is part of the *script launcher*. 
In that way it will be possible to increase the reusability factor of the automation design. 

In this tutorial we will create a script `parameters.1` that will make use of a parameter list that is provided when executing the script.

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
  name: "parameters.1"
  description: "use a parameter defined from the command line using the parameter list option"
  parameters: []
  actions:
  - number: 1
    type: "fwk.outputMessage"
    name: "outputParameter"
    description: "View the parameter value"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "param1: #param1#"
    - name: "onScreen"
      value : "Y"
```

Have a look at the action that we have defined in the above script:
* We make use of the fwk.outputMessage action that allows us to print any message. 
We set the parameter `onScreen: "Y"` so that we can immediately verify the output in the console after execution. 
* We define a message that is composed of a fixed string combined with our parameter `param1` that we enclose using the `#` symbol.

> Reference file name: parameters.1.yml

## Load and execute the script

Now, [load the configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html) into the configuration repository 
and [execute the script](/{{site.repository}}/pages/tutorial/executescript.html). 

```bash
bin/iesi-launch.sh -script parameters.1 -env tutorial
```

You will notice the following output:

```
2019-04-05 07:39:44,806 INFO  [iesi] - action.message=param1:  
```

The fixed string is displayed but no value is displayed for the parameter. We did not pass any value to the execution. 
This can be done through the `-paramlist <arg>` option of the command. Let's run the following command:

```bash
bin/iesi-launch.sh -script parameters.1 -env tutorial -paramlist param1=value1
```

Now, the output reflects the value of the parameter that we provided:

```
2019-04-05 07:43:26,360 INFO  [iesi] - action.message=param1: value1
```


## Update the script and add an additional parameter

In order to add additional parameters the same approach is repeated. We can provide additional `key=value` pairs to the `-paramlist` option by separating pair using `,`:
`-paramlist param1=value1,param2=value2,...`

Update the script to display also a second parameter:

```yaml
    - name: "message"
      value : "param1: #param1# and param2: #param2#"
```

> Reference file name: parameters.1.1.yml

[Load the configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html) into the configuration repository 
and [execute the script](/{{site.repository}}/pages/tutorial/executescript.html). 


```bash
bin/iesi-launch.sh -script parameters.1.1 -env tutorial -paramlist param1=value1,param2=value2
```

This leads to the following output on the screen:

```
2019-04-05 07:49:35,663 INFO  [iesi] - action.message=param1: value1 and param2: value2
```

## Recap

We have now created a script that makes use of several parameters and executed it on the tutorial environment. 
We can now start making use of parameterized actions to create better reusable automation designs.
