{% include navigation.html %}

# Tutorial: Change the environment when executing a script

This page guide you through changing the environment when executing a script. 
When a script is executed, the environment on which the script needs to be executed is mandatory. 
As such, an active environment is available. 

It is possible during the course of execution to change the active environment using the action type `fwk.setEnvironment`.

## Pre-requisites

none

## Add action type to script

For the appropriate script, edit the script design and add an `fwk.setEnvironment` action.

```yaml
  - number: 1
    type: "fwk.setEnvironment"
    name: "envChangeToAcceptance"
    description: "set environment to iesi-acceptance"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "environment"
      value : "iesi-acceptance"
```

This action will change the active environment to *iesi-acceptance*. 
Given that the value is part of the action type, it is possible to add a parameter value here as well, making it possible to receive user or action input. 
If you would like to switch back, a new `fwk.setEnvironment` action needs to be configured. Only 1 environment is active at any given point in time.
