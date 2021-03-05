## sql.executeQuery
## Purpose
This actiontype runs a sql query on a relational database returning a result after execution. Output values can captured after select query execution if required.

*Use Cases*
* Delete data from a table
* Insert data into a table: configuration, reference values, test data
* Create or modify table structures

By using scripts:
* more complex statements are possible
* statements can contain more data
* files can be organized allowing reuse via parameters

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|query|SQL query to run|string|Y|N|
|connection|Connection where to run the SQL query|string|Y|N|
|parameters|Parameters to use for the execution|string|N|N|
|outputDataset|Dataset to write the output to|string|N|N|
|appendOutput|Flag indicating if previous output needs to be removed|string|N|N|

## Example 1
```yaml
  - number: 1
    type: "sql.executeQuery"
    name: "action1"
    description: "select data from Table1"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "select Field1, Field2, Field3 from Table1"
    - name: "connection"
      value : "sql.executeQuery.2"
    - name: "outputDataset"
      value : "dataset1"
```
