## fwk.setIteration
## Purpose
This actiontype defines an interation that can be used in any next action. It represents the definition of where to iterate over.

*Use Cases*
* Define a list of values to iterate over
* Iterate a number of times over a certain action or set of actions
* Iterate over a pre-defined or statically defined list

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|name|Name of the iteration to define|string|N|N|
|type|The type of iteration that will be performed|string|Y|N|
|list|Identifier for the list name that is used to iterate on|string|N|N|
|values|List values to iterate over|string|N|N|
|from|Value to start the iteration|string|N|N|
|to|Value to end the iteration|string|N|N|
|step|alue to use for incrementing or decrementing the iteration|string|N|N|
|condition|Condition to apply for the iteration|string|N|N|
|interrupt|Define if an iteration needs to break if an error inside occurs|string|N|N|


## Example 1
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
## Example 2

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
## Example 3

```yaml
  - number: 3
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
## Example 4

```yaml
  - number: 4
    type: "fwk.setIteration"
    name: "example4"
    description: "Interrupt the iteration upon error"
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
