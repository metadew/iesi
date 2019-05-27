{% include navigation.html %}

# fho.fileExists

This action verifies if a file exists. 

## Use cases

* Verify is a file exists or has been created successfully

## Parameters

### 1: path

`path: "file path location"`
* define the location of the file path where the file to verify is located

### 2: file

`file: "file name to verify"`
* define the name of the file to verify
* if the value for the path is empty, then the full path name in the file that is available will be used.
  * Typically, a single file name can be provided
  * But, if this is not the case, the default file in the jvm is applicable.

### 3: connection

`connection: "connection where the file to verify is located"`
* define the connection name where the file is located
* currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

## Examples

```yaml
  - number: 1
    type: "fho.fileExists"
    name: "Action1"
    description: "Verify file myFile on myPath"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "file"
      value : "myFile"
```

```yaml
  - number: 1
    type: "fho.fileExists"
    name: "Action2"
    description: "Verify file myFile on myPath on a different host"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "flie"
      value : "myFile"
    - name: "connection"
      value : "host.linux.1"
```
