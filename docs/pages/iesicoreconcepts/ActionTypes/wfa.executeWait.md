{% include navigation.html %}
# wfa.executeWait
## Purpose
This actiontype waits (sleeps) for a while and then ends successfully.

*Use Cases*
* Pause between two actions
* Simulate a more human type of behaviour between two actions
* Make use of settings, variables and instructions to make the wait interval random or based on context

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|wait|Number of seconds to wait|number|N|N|

## Example 1
```yaml
  - number: 1
    type: "wfa.executeWait"
    name: "Example1"
    description: "Wait 10 seconds"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "wait"
      value : "10"
```
## Example 2
```yaml
  - number: 2
    type: "wfa.executeWait"
    name: "Example2"
    description: "Wait a random amount of time: between 5 and 20 seconds"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "wait"
      value : "{% raw %}{{*number.between(5,20)}}{% endraw %}"
```
