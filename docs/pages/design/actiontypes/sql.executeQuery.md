{% include navigation.html %}

# sql.executeQuery

This action runs a sql query on a relational database returning a result after execution. 
Output values can captured after select query execution if required.

## Use cases

* Delete data from a table
* Insert data into a table: configuration, reference values, test data
* Create or modify table structures
* By using scripts,
  * more complex statements are possible
  * statements can contain more data
  * files can be organized allowing reuse via parameters

## Parameters

### 1: query

`query: "any query to execute"`
* executes any query
* ouput is only stored in combination with the `outputDataset` parameter
* statements without result will not work, for instance insert and delete queries which are typically being used for unattended operations on the database. 
In this case, the action type `sql.executeStatement` can be used.
* queries can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`

### 2: connection

`connection: "connection where the query needs to be executed on"`
* name of the connection to execute the query on

### 3: outputDataset

`outputDataset: "dataset reference name"`
* name of the dataset where to store the output to
* this parameters only needs to be used in combination with a select statement

### 4: appendOutput

`appendOutput: "Y" / "N"`
* flag to indicate if the output needs to be overwritten

## Examples

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
