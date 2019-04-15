{% include navigation.html %}

# sql.executeQuery

This action executes a sql statement on a relational database. 
No output values are captured after statement execution.

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
* since no ouput is stored, select queries are not advised. We typically see insert and delete queries being used here.
* queries can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`

### 2: connection

`connection: "connection where the query needs to be executed on"`
* name of the connection to execute the query on

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
