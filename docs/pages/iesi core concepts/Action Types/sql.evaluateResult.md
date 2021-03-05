## sql.evaluateResult
## Purpose
This action executes a sql statement on a relational database and evaluates if a result is returned or not. 
If a result is found (count > 0) then the evaluation is successful, otherwise it is considered as an error. 

*Use Cases*
* Verify if data is loaded in a table
* Verify if a table is empty
* Verify if a record has been added to a logging table
* By using scripts,
  * more complex statements are possible
  * files can be organized allowing reuse via parametersr

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|query|SQL query to run|string|Y|N|
|hasResult|Flag indicating if the SQL query should return a result|boolean|Y|N|
|connection|Connection where to run the SQL query|string|Y|N|

*Notes*
* queries can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`
* if count > 0 then hasResult "Y" will return a success status; if count = 0 then it will return an error status. The reverse logic is valid for hasResult "N".

## Example 1
```yaml
  - number: 1
    type: "sql.evaluateResult"
    name: "action1"
    description: "run an evaluation query expecting results"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "select * from Table1"
    - name: "hasResult"
      value : "Y"
    - name: "connection"
      value : "sql.evaluateResult.1"
```
## Example 2
```yaml
---
type: "script"
data:
  name: "sql.evaluateResult.1.2"
  description: "test sql.evaluateResult when data is expected taking input from a file"
  parameters: []
  actions:
  - number: 1
    type: "sql.evaluateResult"
    name: "action1"
    description: "run an evaluation query expecting results taking input from a file"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "{{=file(#iesi.home#/data/iesi-test/actions/data/actions/sql.evaluateResult.1.2.sql)}}"
    - name: "hasResult"
      value : "Y"
    - name: "connection"
      value : "sql.evaluateResult.1"
```
## Example 3
```yaml
---
type: "script"
data:
  name: "sql.evaluateResult.3"
  description: "test sql.evaluateResult when data is expected but not available"
  parameters: []
  actions:
  - number: 1
    type: "sql.evaluateResult"
    name: "action1"
    description: "run an evaluation query expecting results but not available"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "Y"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "select * from Table1"
    - name: "hasResult"
      value : "Y"
    - name: "connection"
      value : "sql.evaluateResult.3"
```
