{% include navigation.html %}
## fwk.setParameterFile
## Purpose
This actiontype loads a file of parameters into the framework's runtime variables. It is integrated with the with the settings and subroutines allowing to derive and lookup values.

*Use Cases*
* Define several parameters at once from a file
* Retrieve an input value from a configuration dataset
* Use a subroutine instruction for getting a specific value

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|filePath|Path where the file(s) need to be located|string|Y|N|
|fileName|File name or expression to check for availability|string|Y|N|
|connection|Connection where the file is located|string|Y|N|

## Example
```yaml
  - number: 1
    type: "fwk.setParameterFile"
    name: "action1"
    description: "set parameter file"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "filePath"
      value : "/myPath"
    - name: "fileName"
      value : "myFileName"
    - name: "connection"
      value : "demo.connection"
```
