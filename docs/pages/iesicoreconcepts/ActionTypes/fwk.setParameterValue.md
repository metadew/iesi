{% include navigation.html %}
## fwk.setParameterValue
## Purpose
This actiontype adds a parameter into the framework's runtime variables. It is integrated with the with the settings and subroutines allowing to derive and lookup values.

*Use Cases*
* Retrieve an input value from a configuration dataset
* Use a subroutine instruction for getting a specific value
* Set a manual configuration value in a script
* Define a parameter at runtime

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|name|Name of the parameter to set as runtime variable|string|Y|N|
|value|Value to set for the runtime variable|string|Y|N|

## Example 1
```yaml
  - number: 1
    type: "fwk.setParameterValue"
    name: "action1"
    description: "set fixed value parameter"
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
```
## Example 2
```yaml
  - number: 2
    type: "fwk.setParameterValue"
    name: "action1"
    description: "set lookup value parameter"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "param1"
    - name: "value"
      value : "{{=connection(myDB,host)}}"
```
