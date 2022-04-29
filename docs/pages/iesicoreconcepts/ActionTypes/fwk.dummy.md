{% include navigation.html %}
## fwk.dummy
## Purpose
This actiontype is a dummy for adding a placeholder step

*Use Cases*
* Placeholder for script design reflections, waiting for the appropriate configuration

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|None|


## Example
```yaml
  - number: 1
    type: "fwk.dummy"
    name: "action1"
    description: "Placeholder for data insert"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters: []
```
