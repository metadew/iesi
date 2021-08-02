{% include navigation.html %}
## sql.executeStatement
## Purpose
This actiontype runs a sql statement on a relational database without returning a result after execution.

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
|statement|SQL statement to run|string|Y|N|
|connection|Connection where to run the SQL statement|string|Y|N|

## Example 1
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
## Example 2
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
## Example 3
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
