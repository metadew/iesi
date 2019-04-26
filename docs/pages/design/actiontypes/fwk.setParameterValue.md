{% include navigation.html %}

# fwk.setParameterValue

This action add a parameter into the framework's runtime variables.  
It is integrated with the with the settings and subroutines allowing to derive and lookup values. 

## Use cases

* Retrieve an input value from a configuration dataset
* Use a subroutine instruction for getting a specific value
* Set a manual configuration value in a script

## Parameters

### 1: name

`name: "name of the parameter"`
* define the name of the parameter

### 2: value

`value: "value for the parameter, including #var# and {{instruction}}"`
* define the value for the parameter
* you can include any function that is resolved by the framework including settings, variables and instructions


## Examples

```yaml
  - number: 1
    type: "fwk.setParameterValue"
    name: "action1"
    description: "set fixed value parameter"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "param1"
    - name: "value"
      value : "value1"
```

```yaml
  - number: 2
    type: "fwk.setParameterValue"
    name: "action1"
    description: "set lookup value parameter"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "param1"
    - name: "value"
      value : "{{=connection(myDB,host)}}"
```
