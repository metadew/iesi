{% include navigation.html %}

# wfa.executeWait

This action waits (*sleeps*) for a while and then ends successfully. 

## Use cases

* Pause between two actions
* Simulate a more human type of behaviour between two actions
* Make use of settings, variables and instructions to make the wait interval random or based on context

## Parameters

### 1: wait

`wait: "number of seconds"`
* Define the number of seconds to wait
  * Wait 10 seconds: 10
  * Wait 2 seconds: 2
  * Etc.
* you can include any function that is resolved by the framework including settings, variables and instructions. 
The only constraint is that the output needs to be a number to define the number of seconds to wait. 
  * Wait between 1 and 5 seconds: {{*number.between(1,5)}}
  * Wait amount of seconds as defined in input parameter `seconds`: #seconds#
  * Wait amount of seconds as stores in the runtime variable `seconds_lookup`: #seconds_lookup#
* if no value is provided, the wait interval will be set to 0

## Examples

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
