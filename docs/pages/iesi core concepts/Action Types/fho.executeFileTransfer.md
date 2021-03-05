## fho.executeFileTransfer
## Purpose
This action transfers one or more files from the localhost to a remote host or vice versa.

*Use Cases*
* Transfer files between locations
* Copy configuration files from the framework to any location

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|sourceFilePath|Path where file(s) to transfer are located|string|Y|N|
|sourceFileName|File name or expression for the file(s) to transfer|string|Y|N|
|sourceConnection|Connection where the file(s) to transfer are located|string|Y|N|
|targetFilePath|Path where the file(s) need to be transfered to|string|Y|N|
|targetFileName|File name or expression for the file(s) to transfer|string|Y|N|
|targetConnection|Connection where the file(s) need to be transfered to|string|Y|N|


## Example
```yaml
  - number: 1
    type: "fho.executeFileTransfer"
    name: "Action1"
    description: "transfer files"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "sourceFilePath"
      value : "/mySourcePath"
    - name: "sourceFileName"
      value : "mySourceFileName"
     - name: "sourceConnection"
      value : "sourceconnection"
    - name: "targetFilePath"
      value : "/myTargetPath"
    - name: "targetFileName"
      value : "myTargetFileName"
    - name: "targetConnection"
      value : "targetconnection"
```
