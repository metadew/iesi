{% include navigation.html %}

# sql.executeStatement

This action runs a sql statement on a relational database without returning a result after execution. 

## Use cases

* Delete data from a table
* Insert data into a table: configuration, reference values, test data
* Create or modify table structures
* By using scripts,
  * more complex statements are possible
  * statements can contain more data
  * files can be organized allowing reuse via parameters

## Parameters

### 1: statement

`statement: "any statement to execute"`
* executes any statement
  * important to note that statements need to be supported by the appropriate jdbc connectivity driver. 
  This is especially the case with respect to data virtualization connectors that are growing in maturity.
* insert and delete statements are typically being used for unattended operations on the database.
* statements can be retrieved from a configuration file using the file lookup instruction `{{=file([filePath]}}`

### 2: connection

`connection: "connection where the statement needs to be executed on"`
* name of the connection to execute the statement on

## Examples

```yaml
  - number: 1
    type: "sql.executeStatement"
    name: "action1"
    description: "delete the value the will be inserted"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "statement"
      value : "delete from Table1 where Field1 = 1000"
    - name: "connection"
      value : "sql.executeStatement.2"
```

```yaml
  - number: 2
    type: "sql.executeStatement"
    name: "action2"
    description: "run a sql insert statement"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "statment"
      value : "insert into Table1 (Field1) values (1000)"
    - name: "connection"
      value : "sql.executeStatement.2"
```

```yaml
  - number: 1
    type: "sql.executeStatement"
    name: "action1"
    description: "run a sql statement from a file"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "statement"
      value : "{{=file(#iesi.home#/data/iesi-test/actions/data/actions/sql.executeStatement.4.1.sql)}}"
    - name: "connection"
      value : "sql.executeStatement.4"
```
