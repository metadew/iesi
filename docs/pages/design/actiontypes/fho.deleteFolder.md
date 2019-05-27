{% include navigation.html %}

# fho.deleteFolder

This action deletes a folder and all its contents. All subfolders and contents are also deleted. 

## Use cases

* Delete a temporary folder
* Reset the initial state before starting a test

## Parameters

### 1: path

`path: "folder path location"`
* define the location of the folder path to delete

### 2: folder

`folder: "folder name or expression to delete"`
* define the name of expression for the folder to delete
* you can include three types of expressions:
  * exact name: this will delete the folder `path + File.separator + folder`
  * `* or blank`: this will delete all folders the can be found in the path. Note that no files in the path will be impacted. 
  * regular expression: this will delete all folders that match the regular expression as specified. 
* if the value for the path is empty, then the full path name in the folder that is available will be used.
  * Typically, a single file name can be provided
  * But, if this is not the case, the default folder in the jvm is applicable.

### 3: connection

`connection: "connection where the folder to delete is located"`
* define the connection name where the folder is located
* currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

## Examples

```yaml
  - number: 1
    type: "fho.deleteFolder"
    name: "Action1"
    description: "Delete folder myFolder on myPath"
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
  - number: 2
    type: "fho.deleteFolder"
    name: "Action2"
    description: "Delete all folders on myPath"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "folder"
      value : "*"
```

```yaml
  - number: 1
    type: "fho.deleteFolder"
    name: "Action1"
    description: "Delete folder myFolder on myPath - alternative approach"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : ""
    - name: "folder"
      value : "/myPath/myFolder"
```