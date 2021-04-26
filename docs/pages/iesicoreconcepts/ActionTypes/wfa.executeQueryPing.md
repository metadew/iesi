{% include navigation.html %}
## wfa.executeQueryPing.md
## Purpose
This actiontype runs a sql query on a relational database waiting for returning a result or not. Output values can captured as runtime variable when a result has been received.

*Use Cases*
* Wait for an event that is logged into a data processing control table (e.g. file arrival, job finished, etc.)
* Wait for another process to clean a table
* Wait for another process to fill a table with data
* Design a triggering mechanism for engineers to add requests to a table (via simple UI or just plain insert statements)

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|query|SQL query to run|string|Y|N|
|hasResult|Flag indicating if the SQL query should retun a result|string|Y|N|
|setRuntimeVariables|Flag indicating if an expected result will be set as a runtime|string|N|N|
|connection|Connection where to run the SQL query|string|Y|N|
|wait|Number of seconds to wait between checks|number|N|N|
|timeout|Number of seconds to wait for a result|string|N|N|

*Notes*
* statements without result will not work, for instance insert, update and delete queries.
* queries can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`
* the query allow the use of the * wildcard as in `select * from ...`. In this case the column names returned by the database will be used automatically as variable names. 
* leave the timeout blank to define an eternal wait - default value is no timeout


## Example
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
