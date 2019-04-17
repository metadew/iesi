{% include navigation.html %}

# fwk.setParameterList

This action add a list of parameters into the framework's runtime variables.  
It is integrated with the with the settings and subroutines allowing to derive and lookup values. 

## Use cases

* Define several parameters at once
* Retrieve an input value from a configuration dataset
* Use a subroutine instruction for getting a specific value
* Set a manual configuration value in a script

## Parameters

### 1: list

`list: "list of parameters and values to define"`
* define the list of parameters and values using the following syntax `parameter1=value1,parameter2=value2,...`
* the `parameter=value` pairs are seperated using columns


## Examples

```yaml
  - number: 1
    type: "fwk.setParameterList"
    name: "action1"
    description: "set parameter list"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "param1=value1,param2=value2"
```

```yaml
  - number: 1
    type: "fwk.setParameterList"
    name: "action1"
    description: "set parameter list with an instruction"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "param1=value1,param2=value2,param3={{$fwk.version}}"
```
