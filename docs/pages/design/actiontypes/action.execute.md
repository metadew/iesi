{% include navigation.html %}

# action.execute

This action (re-)executes another action inside the current script. 
During execution it makes use of the action and its parameters. 
The conditional expression is also taken into account; the iteration not. This is a future improvement that will be added later. 

## Use cases

* Define repetitive actions only once in a script, not requiring to create another script

## Parameters

### 1: name

`name: "name of the action "`
* define the name of the action to execute
* the mechanics of the action calling a child action remain unchanged
* the child action will be executed with all functions, except iteration
* the action name is *not* case sensitive

## Examples

```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "Action1"
    description: "Action1"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "action1"
    - name: "onScreen"
      value : "Y"
  - number: 2
    type: "action.execute"
    name: "Action2"
    description: "Action2"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "Action1"
```
