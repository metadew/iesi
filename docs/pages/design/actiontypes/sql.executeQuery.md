{% include navigation.html %}

# sql.executeQuery

This action executes a sql statement on a relational database. 
Output values can captured after select statement execution if required.

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
* since ouput is only stored in combination with the `outputDataset` parameter, select queries are only advised in the case. 
* insert and delete queries are typically being used for unattended operations on the database.
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
    description: "delete the value the will be inserted"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "delete from Table1 where Field1 = 1000"
    - name: "connection"
      value : "sql.executeQuery.2"
```

```yaml
  - number: 2
    type: "sql.executeQuery"
    name: "action2"
    description: "run a sql insert query"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "insert into Table1 (Field1) values (1000)"
    - name: "connection"
      value : "sql.executeQuery.2"
```

```yaml
  - number: 1
    type: "sql.executeQuery"
    name: "action1"
    description: "run a sql query from a file"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "query"
      value : "{{=file(#iesi.home#/data/iesi-test/actions/data/actions/sql.executeQuery.4.1.sql)}}"
    - name: "connection"
      value : "sql.executeQuery.4"
```
