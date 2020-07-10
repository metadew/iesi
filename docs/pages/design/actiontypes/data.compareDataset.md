{% include navigation.html %}

# data.compareDataset

This action allows you to compare two datasets. 


## Use cases

* When receiving the response of an http request you can compare the actual dataset to a predefined dataset.

## Parameters

### 1: leftDataset

`leftDataset: "name of the dataset to compare"`
* Dataset to compare

### 2: rightDataset

`rightDataset: "name of the dataset to compare against"`
* Dataset to compare against

### 3: mapping

Mapping allows to circumvent any mismatch in keys between the two datasets.

## Examples

```yaml
---
type: Script
data:
  type: "script"
  name: "data.compareDataset.1"
  description: "test data.compareDataset"
  actions:
  - number: 1
    type: "data.setDatasetConnection"
    name: "set expected dataset connection"
    description: "default"
    errorExpected: "N"
    errorStop: "Y"
    parameters:
    - name: "name"
      value : "expected"
    - name: "dataset"
      value : "compareDataset"
    - name: "labels"
      value : "expected"
  - number: 1
    type: "data.setDatasetConnection"
    name: "set actual dataset connection"
    description: "default"
    errorExpected: "N"
    errorStop: "Y"
    parameters:
    - name: "name"
      value : "actual"
    - name: "dataset"
      value : "compareDataset"
    - name: "labels"
      value : "actual"
  - number: 3
    type: "data.compareDataset"
    name: "compare expected with actual dataset"
    description: "default"
    errorExpected: "N"
    errorStop: "Y"
    parameters:
    - name: "leftDataset"
      value : "expected"
    - name: "rightDataset"
      value : "actual"
    - name: "mapping"
      value : "datasetCompare.1"
```
