{% include navigation.html %}

# sql.setRuntimeVariables

This action executes a sql statement on a relational database and stores the result as runtime variables that can be used by other actions. 
The column names in the sql statement will be used as variable name, the query result as variable value. 
Therefore, only 1 row is expected to be returned as result. 

## Use cases

* Store dynamic data as variables for use in other actions:
  * run identifiers
  * row counts
  * etc.

## Parameters

### 1: query

`query: "any query to execute"`
* executes any query, but requires a select statement to function
* only 1 row is expected as result
* as the column name from the query is used as variable name, specifically naming the column using an alias in the query is advised to have control over the variable name in other actions: `select count(*) as 'COUNT' ...`
* the query allow the use of the * wildcard as in `select * from ...`. In this case the column names returned by the database will be used automatically as variable names. 
This can be useful when dealing with stable, large table definitions but remains a risk and needs to be well considered.

### 2: connection

`connection: "connection where the query needs to be executed on"`
* name of the connection to execute the query on

## Examples

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
