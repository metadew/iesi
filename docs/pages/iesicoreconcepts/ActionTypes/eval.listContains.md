{% include navigation.html %}
## eval.listContains
## Purpose
This actiontype evaluates if value exists in list

*Use Cases*
* Evaluate input/output values
* Validate expected outcome of calls/executions retrieved in list

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|list|Expected value to evaluate|string|Y|N|
|value|Actual value to evaluate|string|Y|N|

## Example 1
```yaml
  - number: 1
    type: "eval.listContains"
    name: "action1"
    description: "evaluate output"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "{{^list(1, 2, 3, 4)}}"
    - name: "value"
      value : "1"
```

## Example 2
```yaml
  - number: 2
    type: "eval.listContains"
    name: "action1"
    description: "evaluate output"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "{{^list({{^dataset(compareDataset, {{^list(expected)}})}})}}"
    - name: "value"
      value : "{{^template(template1, 1)}}"
```
