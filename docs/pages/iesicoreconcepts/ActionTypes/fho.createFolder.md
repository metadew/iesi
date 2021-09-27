{% include navigation.html %}
## fho.createFolder
## Purpose
This actiontype creates a folder

*Use Cases*
* Create a new folder
* Store output in new/unique folder

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|path|Path where the folder will be located|string|N|N|
|folder|Folder to create|string|Y|N|
|connection|Connection where the folder will be located|string|N|N|

* Currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

* If the folder already exists, no action will be taken

## Example
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
