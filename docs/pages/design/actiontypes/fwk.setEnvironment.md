{% include navigation.html %}

# fwk.setEnvironment

This action changes the environment on which the script execution has been initiated for.
It is integrated with the framework's execution engine that manages the connectivity.

## Use cases

* Start a script on 1 environment and continue on a second environment
* Compare two environmments

## Parameters

### 1: environment

`environment: "environment name"`
* select the appropriate environment to change to. This environment needs to be defined in the framework.
* the previous environment is no longer maintained.
If an action needs to be performed on the previous environment, a new `fwk.setEnvironment` action needs to be performed.
* if the selected environment does not exist, the action will end in error. So, if you want to stop the script when the environment does not exist, you need to make use of the stop on error flag.

## Examples

```yaml
  - number: 1
    type: "fwk.setEnvironment"
    name: "example1"
    description: "Set environment to tutorial"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "environment"
      value : "tutorial"
```
