{% include navigation.html %}
## sql.setRuntimeVariables
## Purpose
This actiontype executes a sql statement on a relational database and stores the result as runtime variables that can be used by other actions. The column names in the sql statement will be used as variable name, the query result as variable value. Therefore, only 1 row is expected to be returned as result.

*Use Cases*
* Store dynamic data as variables for use in other actions: run identifiers - row counts - ...

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|query|SQL Stored Procedure to run|string|Y|N|
|connection|Connection where to run the SQL Stored Procedure|string|Y|N|

## Example 1
```yaml
  - number: 1
    type: "sql.setRuntimeVariables"
    name: "action1"
    description: "run a sql select query and store results as runtime variables"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "select Field1, Field2, Field3 from Table1 where Field1 = 1"
    - name: "connection"
      value : "sql.setRuntimeVariables.1"
```
## Example 2
```yaml
  - number: 1
    type: "sql.setRuntimeVariables"
    name: "action1"
    description: "run a sql select query taking input from a file"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "{{=file(#iesi.home#/data/iesi-test/actions/data/actions/sql.setRuntimeVariables.3.1.sql)}}"
    - name: "connection"
      value : "sql.setRuntimeVariables.3"
```
