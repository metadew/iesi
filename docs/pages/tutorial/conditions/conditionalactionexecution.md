{% include navigation.html %}

# Tutorial: Conditional action execution

This page guide you through adding conditional evaluations for actions that are defined in a script. 
Sometimes you want to verify something before executing an action. 
This can be to ensure that all pre-requisites are fullfilled or to skip an action if it is not relevant (e.g. type of environment, host, etc.). 
In that way is possible to tune the scripts in line with the context during execution. 

In this tutorial we will create a script `hellocondition` containing a conditional evaluation for an action to execute or not based on an input parameter. 

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.

## Create the base script

First, we will create a script that contains two actions that will ouput a message on the screen. 
* Open a text editor and create a new file `HelloCondition.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: "script"
data:
  name: "hellocondition"
  description: "Hello Condition Tutorial Script"
  parameters: []
  actions:
  - number: 1
    type: "fwk.outputMessage"
    name: "Action1"
    description: "First action"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "First action"
    - name: "onScreen"
      value : "Y"
  - number: 2
    type: "fwk.outputMessage"
    name: "Action2"
    description: "Second action"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "Second action"
    - name: "onScreen"
      value : "Y"
```

Now, [load the configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html) into the configuration repository 
and [execute the script](/{{site.repository}}/pages/tutorial/executescript.html). 

```bash
bin/iesi-launch.sh -script hellocondition -env tutorial
```

You will see that both actions are executed as defined.

```
2019-04-08 13:56:42,610 INFO  [iesi] - action.name=Action1
2019-04-08 13:56:42,966 INFO  [iesi] - action.message=First action
2019-04-08 13:56:42,986 INFO  [iesi] - action.status=SUCCESS
2019-04-08 13:56:42,989 INFO  [iesi] - action.name=Action2
2019-04-08 13:56:43,117 INFO  [iesi] - action.message=Second action
2019-04-08 13:56:43,137 INFO  [iesi] - action.status=SUCCESS
```

## Add a condition for an action

Next, we will create a condition for the first action. 
The condition will be evaluated, if `true` then the action will be executed, otherwise the action will not be executed. 
We can make use of `jexl` or `javascript` to define the condition. 

Consider the below action, the evaluation will return `true` and the action will be executed.

```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "Action1"
    description: "First action"
    component: ""
    condition: "1 == 1"
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "First action"
    - name: "onScreen"
      value : "Y"
```

Alternative for the below action, the evaluation will return `false` and the action will be skipped.

```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "Action1"
    description: "First action"
    component: ""
    condition: "1 == 0"
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "First action"
    - name: "onScreen"
      value : "Y"
```

Notice the corresponding output:

```
2019-04-08 14:03:36,857 INFO  [iesi] - action.name=Action1
2019-04-08 14:03:37,253 INFO  [iesi] - action.status=SKIPPED
```

Taking it further, the evaluation can make use of settings, parameters and instructions making it variable and relative to the execution context.


## Add a condition taking input from a command line parameter

Update the script above and add a parameter in the evaluation. 
Parameters are defined using the `#` symbol before and after its name: `#variable#`. 

Consider the below action which will be executed if the parameter `param1` is passed with value 1 to the script execution. 
Otherwise the action will not be executed. 
Important to note is that by default the action will execute, even if the condition cannot be parsed or evaluated. 

```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "Action1"
    description: "First action"
    component: ""
    condition: "#param1# == 1"
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "First action"
    - name: "onScreen"
      value : "Y"
```

## Recap

We have now created a script that makes use of conditions to evaluate whether or not an action will be executed. 
The next step will be to take this concept further and integrate it with other reusability constructs: parameters, variables, output, etc. 
