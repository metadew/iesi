## fho.fileExists
## Purpose
This actiontype verifies if a file exists

*Use Cases*
* Verify if a file exists or has been successfully created 

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|path|Path where the file is located|string|N|N|
|file|File to verify|string|Y|N|
|connection|Connection where the file is located|string|N|N|

* Currently on ssh type remote connections will work
  * if the parameter is empty, or defined as `localhost` then the process will be executed locally by the framework's user
  * if the connection is located remotely, then a ssh connection will be established
  * if the connection parameter `allowLocalhostExecution` is set to `N` then ssh execution will be forced

* If the folder already exists, no action will be taken


## Example 1

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
## Example 2

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
