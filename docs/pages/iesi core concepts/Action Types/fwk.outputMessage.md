## fwk.outputMessage
## Purpose
This actiontype prints a message that can be used for logging or debugging purposes. It is integrated with the framework's execution engine that manages settings and runtime variables. 

*Use Cases*
* Print a value of a variable for auditing purposes
* Print additonal information on what the script is doing at runtime
* Can be combined with the conditional construct to increase logging based on decisions

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|message|Message to be printed|string|N|N|
|onScreen|Flag indicating if the message needs to be printed on the screen|string|N|N|


## Example 1
```yaml
  - number: 1
    type: "fwk.outputMessage"
    name: "example1"
    description: "Free text on screen"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "Free text example"
    - name: "onScreen"
      value: "Y"
```
## Example 2
```yaml
  - number: 2
    type: "fwk.outputMessage"
    name: "example1"
    description: "Adding variables in the message"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "#count# records have been found"
    - name: "onScreen"
      value: "Y"
```
