## fho.deleteFile
## Purpose
This actiontype deletes one or more files in a folder.

*Use Cases*
* Cleanup of files in specific folder(s)

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|path|Path where the file is located|string|N|N|
|file|File(s) to delete|string|Y|N|
|connection|Connection where the file is located|string|N|N|


## Example
```yaml
  - number: 1
    type: "fho.deleteFile"
    name: "Action1"
    description: "Delete one or more files in a folder"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "path"
      value : "/myPath"
    - name: "file"
      value : "myFolder"
```
