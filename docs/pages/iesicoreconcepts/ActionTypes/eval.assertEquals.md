{% include navigation.html %}
## eval.assertEquals
## Purpose
This actiontype evaluates if two values are equal

*Use Cases*
* Evaluate input/output values
* Validate expected outcome of calls/executions

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|expected|Expected value to evaluate|string|Y|N|
|actual|Actual value to evaluate|string|Y|N|

## Example
```yaml
  - number: 1
    type: "eval.assertEquals"
    name: "evaluateOutput"
    description: "validate if expected output is equal to actual output"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "expected"
      value : "1"
    - name: "actual"
      value : "{{=dataset(sepatransferOutput,id)}}"
```
