{% include navigation.html %}

# script.logOutput

This action stores an output value as part of the script results. 
It allows to log script related outputs specifically in the script output for reporting or processing purposes.

## Use cases

* Add a categorization to a script's execution
* Log metrics regarding the testing process for reporting purposes later onwards (across different actions)

## Parameters

### 1: name

`name: "output name"`
* define the output name to use for storing the value in the `ScriptResults` table
* this parameter is mandatory, otherwise the logging cannot take place

### 2: value

`value: "output value"`
* define the output value to store in the `ScriptResults` table
* make use of any function that is resolved by the framework including settings, variables and instructions

## Examples

```yaml
  - number: 1
    type: "script.logMessage"
    name: "example1"
    description: "Log parameter param1"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "param1"
    - name: "value"
      value: "#param1#"
```
