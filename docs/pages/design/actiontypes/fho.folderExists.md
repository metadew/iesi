{% include navigation.html %}

# fho.folderExists

This action verifies if a folder exists. 

## Use cases

* Verify is a folder exists or has been created successfully

## Parameters

### 1: path

`path: "folder path location"`
* define the location of the folder path where the folder to verify is located

### 2: folder

`folder: "folder name to verify"`
* define the name of the folder to verify
* if the value for the path is empty, then the full path name in the folder that is available will be used.
  * Typically, a single file name can be provided
  * But, if this is not the case, the default folder in the jvm is applicable.

### 3: connection

`connection: "connection where the folder to verify is located"`
* define the connection name where the folder is located
* currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

## Examples

```yaml
  - number: 1
    type: "fho.folderExists"
    name: "Action1"
    description: "Verify folder myFolder on myPath"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "folder"
      value : "myFolder"
```

```yaml
  - number: 1
    type: "fho.folderExists"
    name: "Action2"
    description: "Verify folder myFolder on myPath on a different host"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "folder"
      value : "myFolder"
    - name: "connection"
      value : "host.linux.1"
```
