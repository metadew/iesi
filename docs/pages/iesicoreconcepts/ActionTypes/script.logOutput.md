{% include navigation.html %}
## script.logOutput
## Purpose
This actiontype stores an output value as part of the script results. It allows to log script related outputs specifically in the script output for reporting or processing purposes.

*Use Cases*
* Add a categorization to a script's execution
* Log metrics regarding the testing process for reporting purposes later onwards (across different actions)

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|name|Name of the output|string|Y|N|
|value|Value for the output|string|Y|N|


## Example
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
