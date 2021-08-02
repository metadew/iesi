{% include navigation.html %}
## fho.deleteFolder
## Purpose
This action deletes a folder and all its contents. All subfolders and contents are also deleted.

*Use Cases*
* Delete a temporary folder
* Reset the initial state before starting a test

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|path|Path where the folder is located|string|N|N|
|folder|Folder to create|string|Y|N|
|connection|Connection where the folder will be located|string|N|N|

* Currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

* If the folder already exists, no action will be taken

## Example 1

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
## Example 2

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
## Example 3

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
      va
