{% include navigation.html %}
## fwk.setParameterList
## Purpose
This actiontype adds a list of parameters into the framework's runtime variables. It is integrated with the with the settings and subroutines allowing to derive and lookup values. 

*Use Cases*
* Define several parameters at once
* Retrieve an input value from a configuration dataset
* Use a subroutine instruction for getting a specific value
* Set a manual configuration value in a script

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|list|List of parameters and values to set as runtime variables|string|Y|N|

## Example 1
```yaml
  - number: 1
    type: "fwk.setParameterList"
    name: "action1"
    description: "set parameter list"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "param1=value1,param2=value2"
```
## Example 2

```yaml
  - number: 1
    type: "fwk.setParameterList"
    name: "action1"
    description: "set parameter list with an instruction"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "param1=value1,param2=value2,param3={{$fwk.version}}"
```
