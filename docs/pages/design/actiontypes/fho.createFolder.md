{% include navigation.html %}

# fho.createFolder

This action creates a folder. If the folder already exists, no action is taken.

## Use cases

* Create a new folder

## Parameters

### 1: path

`path: "folder path location"`
* define the location of the folder path to create

### 2: folder

`folder: "folder name"`
* define the name for the folder to create
* if the value for the path is empty, then the full path name in the folder that is available will be used.
  * Typically, a single file name can be provided
  * But, if this is not the case, the default folder in the jvm is applicable.

### 3: connection

`connection: "connection where the folder to create is located"`
* define the connection name where the folder is located
* currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

## Examples

```yaml
  - number: 1
    type: "fho.createFolder"
    name: "Action1"
    description: "create folder myFolder on myPath"
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
