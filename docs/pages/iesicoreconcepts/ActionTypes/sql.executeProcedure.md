{% include navigation.html %}
## sql.executeProcedure
## Purpose
This actiontype executes a sql stored procedure on a relational database. The input parameters can be provided as one of the parameters. Output values can captured after select statement execution if required.

*Use Cases*
* Execute a sql stored procedure that is defined as api to the solution

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|procedure|SQL Stored Procedure to run|string|Y|N|
|connection|Connection where to run the SQL Stored Procedure|string|Y|N|
|parameters|Parameters to use for the execution|string|N|N|
|outputDataset|Dataset to write the output to|string|N|N|
|appendOutput|Flag indicating if previous output needs to be removed|string|N|N|

## Example 1
```yaml
  - number: 1
    type: "sql.executeProcedure"
    name: "Action1"
    description: "Execute a procedure to return all data"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "procedure"
      value : "GetTable1All"
    - name: "connection"
      value : "db.mssql.1"
```
## Example 2
```yaml
  - number: 1
    type: "sql.executeProcedure"
    name: "Action1"
    description: "Execute a procedure to return data for a specific Field1 value"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "procedure"
      value : "GetTable1ByField1"
    - name: "connection"
      value : "db.mssql.1"
    - name: "parameters"
      value : "@Field1=1"
```
