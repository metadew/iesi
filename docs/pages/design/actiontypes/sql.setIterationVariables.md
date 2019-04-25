{% include navigation.html %}

# sql.setIterationVariables

This action executes a sql statement on a relational database and stores the result as iteration variables that can be used by iterations further in the script. 
The column names in the sql statement will be used as variable name, the query result as variable value. 
Given that the result will be used to iterate over, every row that is returned is considered as a new iteration. 
Every column and its value will be stored as an iteration variable part of their iteration scope. 

## Use cases

* Iterate dynamically over a set of data that is stored in a table:
  * component list
  * specific selection of items
  * etc.

## Parameters

### 1: list

`list: "reference name for the list"`
* the reference name is used when setting the iteration using the ´fwk.setIteration` action

### 1: query

`query: "any query to execute"`
* executes any query, but requires a select statement to function
* can contain multiple rows, where every row is considered as a new iteration. 
The order of the select statement will determine the order of iterating.
* as the column name from the query is used as variable name, specifically naming the column using an alias in the query is advised to have control over the variable name in other actions: `select count(*) as 'COUNT' ...`
* the query allow the use of the * wildcard as in `select * from ...`. In this case the column names returned by the database will be used automatically as variable names. 
This can be useful when dealing with stable, large table definitions but remains a risk and needs to be well considered.

### 2: connection

`connection: "connection where the query needs to be executed on"`
* name of the connection to execute the query on

## Examples

```yaml
  - number: 1
    type: "sql.setIterationVariables"
    name: "action1"
    description: "run a sql select query and store results as iteration variables"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "list"
      value : "List1"
    - name: "query"
      value : "select Field1, Field2, Field3 from Table1"
    - name: "connection"
      value : "sql.setIterationVariables.1"
```
