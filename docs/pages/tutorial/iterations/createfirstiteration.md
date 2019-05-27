{% include navigation.html %}

# Tutorial: Create a first iteration

This page guides you through creating your first iteration. 

In this tutorial we will create a script `helloiteration` having a simple iteration and apply it to an automatically generated output message. 

## Pre-requisites

* The tutorial environment has been created. See the tutorial [create tutorial environment](/{{site.repository}}/pages/tutorial/tutorialenvironment.html) for more information.

## Create the base script

First, we will create a script that contains two actions, one to define the iteration and one to iterate over. 
For this we will make use of the action type fwk.outputMessage that will ouput a message on the screen. 
If we leave the message blank, the framework will generate an automated message for us. 
* Open a text editor and create a new file `HelloIteration.yml`
* Edit the configuration file and add the configuration for this script:

```yaml
---
type: "script"
data:
  name: "helloiteration"
  description: "Hello Iteration Tutorial Script"
  parameters: []
  actions:
  - number: 1
    type: "fwk.setIteration"
    name: "Action1"
    description: "Define the iteration"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value: "Iteration1"
    - name: "type"
      value: "for"
    - name: "from"
      value: "1"
    - name: "to"
      value: "3"
  - number: 2
    type: "fwk.outputMessage"
    name: "Action2"
    description: "Apply the iteration on an action"
    component: ""
    condition: ""
    iteration: "Iteration1"
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : ""
    - name: "onScreen"
      value : "Y"
```

Now, [load the configuration](/{{site.repository}}/pages/tutorial/loadconfiguration.html) into the configuration repository 
and [execute the script](/{{site.repository}}/pages/tutorial/executescript.html). 

```bash
bin/iesi-launch.sh -script helloiteration -env tutorial
```

You will see that the second action will be executed 3 times.

```
2019-04-12 16:55:27,052 INFO  [iesi] - action.name=Action2
2019-04-12 16:55:27,223 INFO  [iesi] - action.message=Still waiting...
2019-04-12 16:55:27,246 INFO  [iesi] - action.status=SUCCESS
2019-04-12 16:55:27,255 INFO  [iesi] - action.name=Action2
2019-04-12 16:55:27,396 INFO  [iesi] - action.message=Where have you been all my life?
2019-04-12 16:55:27,416 INFO  [iesi] - action.status=SUCCESS
2019-04-12 16:55:27,423 INFO  [iesi] - action.name=Action2
2019-04-12 16:55:27,647 INFO  [iesi] - action.message=This is so cool!
2019-04-12 16:55:27,667 INFO  [iesi] - action.status=SUCCESS
```

*Note that the message can differ since the generation is random.*

Now, you can alter the iteration definition, taking a look at the [action type page](/{{site.repository}}/pages/design/actiontypes/fwk.setIteration.html).

## Recap

We have now created a script that makes use of an iteration to execute an action multiple times. 
The next step will be to take this concept further and integrate it with other reusability constructs: parameters, variables, output, etc. 
