{% include navigation.html %}

# fwk.outputMessage

This action prints a message that can be used for logging or debugging purposes. 
It is integrated with the framework's execution engine that manage settings and runtime variables. 

## Use cases

* Print a value of a variable for auditing purposes
* Print additonal information on what the script is doing at runtime
* Can be combined with the conditional construct to increase logging based on decisions

## Parameters

### 1: message

`message: "any message including #var# and {{instruction}}"`
* prints any message that is entered
* you can include any function that is resolved by the framework including settings, variables and instructions
* if no value is provided, a default automation message of the day will be generated

For instance:
* iesi.home is set to location #iesi.home#

### 2: onScreen

`onScreen: "Y" / "N"`
* if `Y` is selected, the message will appear both on screen as well as in the log file. If `N`is selected, the message will only appear in the log file.
* if no value is provided, the message will not be shown on the screen


## Examples

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

```yaml
  - number: 2
    type: "fwk.outputMessage"
    name: "example1"
    description: "Free text not on screen"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "Free text example"
    - name: "onScreen"
      value: "N"
```

```yaml
  - number: 3
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