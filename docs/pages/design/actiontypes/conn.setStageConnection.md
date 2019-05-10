{% include navigation.html %}

# conn.setStageConnection

This action sets a stage connection. A stage connection is a temporary connection that results in a Sqlite database file. 
This file is stored in the `run/tmp/stage` folder can be accessed as a connection.

## Use cases

* store temporary data or results
* run tests across databases or formats

## Parameters

### 1: stage

`stage: "name of the stage connection"`
* name of the stage connection

### 2: cleanup

`cleanup: "Y" / "N"`
* if `Y` is selected, the stage connection will be deleted after processing. If `N` is selected, it will be persisted and can be reused in a next run.

## Examples

```yaml
  - number: 1
    type: "conn.setStageConnection"
    name: "Action1"
    description: "Define the stage connection"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "stage"
      value : "myStage"
    - name: "cleanup"
      value: "Y"
```
