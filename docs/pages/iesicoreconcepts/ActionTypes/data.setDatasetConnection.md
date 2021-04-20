## data.setDatasetConnection
## Purpose
This actiontype defines a dataset connection that can be used in any next action  

*Use Cases*
* Define a dataset to store input/output of your executing action
* Define a dataset to loop through set of parameters
* Define a dataset to store environment specific input

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|name|Reference name for the dataset definition|string|Y|N|
|type|Type of dataset|string|N|N|
|dataset|Dataset to use|string|Y|N|        
|labels|Labels associated to the dataset instance|string|N|N|

## Example
```yaml
  - number: 1
    type: "data.setDatasetConnection"
    name: "setTransferDataset"
    description: "define my input dataset for transfer"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "sepatransferInput"
    - name: "dataset"
      value : "sepatransfer"
    - name: "labels"
      value : "demo,input"
```

```yaml
  - number: 2
    type: "data.setDatasetConnection"
    name: "setTransferDataset"
    description: "define my output dataset for transfer"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "sepatransferOutput"
    - name: "dataset"
      value : "sepatransfer"
    - name: "labels"
      value : "{{$run.id}},output"
```
