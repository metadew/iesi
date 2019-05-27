{% include navigation.html %}

# wfa.executeQueryPing

This action runs a sql query on a relational database waiting for returning a result or not. 
Output values can captured as runtime variable when a result has been received.

## Use cases

* Wait for an event that is logged into a data processing control table (e.g. file arrival, job finished, etc.)
* Wait for another process to clean a table
* Wait for another process to fill a table with data
* Design a triggering mechanism for engineers to add requests to a table (via simple UI or just plain insert statements)

## Parameters

### 1: query

`query: "any query to execute"`
* executes any query that returns a result. A `select` query is required in order to evaluate the result.
* statements without result will not work, for instance insert, update and delete queries. 
* queries can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`

### 2: hasResult

`hasResult: "Y" / "N"`
* define if a result is expected or not
* if hasResult "Y" then the action will wait for count > 0. The reverse logic is valid for hasResult "N", the action will wait for count = 0.

### 3: setRuntimeVariables

`setRuntimeVariables: "Y" / "N"`
* define if a result needs to be set a runtime variables
* as a result is expected, this action is only relevant when `hasResult="Y"`
* only 1 row is expected as result
* as the column name from the query is used as variable name, specifically naming the column using an alias in the query is advised to have control over the variable name in other actions: `select count(*) as 'COUNT' ...`
* the query allow the use of the * wildcard as in `select * from ...`. In this case the column names returned by the database will be used automatically as variable names. 
This can be useful when dealing with stable, large table definitions but remains a risk and needs to be well considered.

### 4: connection

`connection: "connection where the query needs to be executed on"`
* name of the connection to execute the query on

### 5: wait

`wait: "number of seconds"`
* define the number of seconds to wait between checks
* default value is 1 second

### 6: wait

`timeout: "number of seconds"`
* define the number of seconds to wait for a result
* leave the timeout blank to define an eternal wait
* default value is no timeout

## Examples

```yaml
  - number: 1
    type: "wfa.executeQueryPing"
    name: "action1"
    description: "Wait for a record in Table1"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "select Field1, Field2, Field3 from Table1"
    - name: "hasResult"
      value : "Y"
    - name: "setRuntimeVariables"
      value : "Y"
    - name: "connection"
      value : "sql.executeQuery.2"
    - name: "wait"
      value : "10"
    - name: "timeout"
      value : "1200"
```
