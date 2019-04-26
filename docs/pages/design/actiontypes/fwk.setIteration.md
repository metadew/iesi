{% include navigation.html %}

# fwk.setIteration

This action defines an interation that can be used in any next action.
It represents the definition of where to iterate over.

## Use cases

* Define a list of values to iterate over
* Iterate a number of times over a certain action or set of actions
* Iterate over a pre-defined or statically defined list

## Parameters

### 1: name

`name: "name of the iteration"`
* define the name of the iteration that can be used in any next action as a reference
* this name will be associated with the other parameter values that are defined

### 2: type

`type: "values" / "for" / "condition"`
* define the type of iteration to use:
  * values: iterate over a list of values -> complete parameter values
  * for: iterate over a set of numbers -> complete parameters from, to, step
  * condition: iteration as long as a condition returns `true` -> complete parameter condition

### 3: values

`values: "values to iterate over"`
* applicable for type `values`
* define the values that will be iterated over
* different values are delimited with a `,` symbol. For instance: value1,value2 -> this will iterate twice, once for value1 and once for value2.

### 4: from

`from: "value to start the iteration"`
* applicable for type `for`
* define the **integer** value for starting the iteration
* the from value is included in the iteration evaluation

### 5: to

`to: "value to end the iteration"`
* applicable for type `for`
* define the **integer** value for stopping the iteration
* the to value is included in the iteration evaluation

### 6: step

`step: "value to use for incrementing or decrementing the iteration"`
* applicable for type `for`
* define the **integer** value for incrementing or decrementing the iteration
* if no step value is provided, this will be set by default to `1`

### 7: condition

`list: "identifier for the list name that is used to iterate on"`
* applicable for type `condition`
* define the condition to evaluate in the iteration
* the iteration will continue as long as the condition is `true`

### 8: list

`list: "identifier for the list name that is used to iterate on"`
* applicable for type `list`
* define the list name will be used for the iteration
* lists can be filled using one of the following action types:
  * sql.setIterationVariables

### 9: interrupt

`interrupt: "Y" / "N"`
* define if an iteration needs to break if an error inside occurs
* this can be achieved by setting the value to `Y`
* by default the value is set to `N`


## Examples

### Define an iteration using a list of values

```yaml
  - number: 1
    type: "fwk.setIteration"
    name: "example1"
    description: "Define an iteration using a list of values"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "iteration1"
    - name: "type"
      value: "values"
    - name: "values"
      value: "value1,value2,value3"
```

### Define an iteration using a for loop

```yaml
  - number: 2
    type: "fwk.outputMessage"
    name: "example2"
    description: "Define an iteration using a for loop"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "iteration2"
    - name: "type"
      value: "for"
    - name: "from"
      value: "1"
	- name: "to"
      value: "5"
	- name: "step"
      value: "1"
```

### Define an iteration using a list

```yaml
  - number: 1
    type: "fwk.setIteration"
    name: "example3"
    description: "Define an iteration using a list"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "iteration1"
    - name: "type"
      value: "list"
    - name: "list"
      value: "list1"
```

### Interrupt the iteration upon error

```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "example4"
    description: "Define an iteration using a for loop"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "iteration2"
    - name: "type"
      value: "for"
    - name: "from"
      value: "1"
	- name: "to"
      value: "5"
	- name: "step"
      value: "1"
	- name: "interrupt"
      value: "Y"
```
