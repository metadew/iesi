{% include navigation.html %}
## fwk.setEnvironment
## Purpose
This actiontype changes the environment on which the script execution has been initiated for. It is integrated with the framework's execution engine that manages the connectivity.

*Use Cases*
* Start a script on environment X and continue on environment Y
* Compare two environmments

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|environment|Environment name where the next action will be executed on|string|N|N|

## Example
```yaml
  - number: 1
    type: "fwk.setEnvironment"
    name: "example1"
    description: "Set environment 'production' to environment 'tutorial'"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "environment"
      value : "tutorial"
```
