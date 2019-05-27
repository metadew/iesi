{% include navigation.html %}

# db.presto

This connection is for Presto Distributed SQL Query Engine.

Get more information at [http://prestodb.github.io](http://prestodb.github.io).

## Use cases

* SQL query engine abstraction
* Big data testing

## Parameters

### 1: host

`host: "host name"`
* define the host name to connect to

### 2: port

`port: "port number"`
* define the port number to connect to
* this parameter is optional

### 3: catalog

`catalog: "catalog name"`
* define the catalog to connect to

### 4: schema

`schema: "schema name"`
* define the optional schema name in the catalog to connect to

### 5: user

`user: "user name"`
* define the user name to use for the establishing the connection

### 6: password

`password: "user password"`
* define the user password to use for establishing the connection

## Examples

```yaml
---
type: Connection
data:
  name: "db.presto.1"
  type: "db.presto"
  description: "db.presto connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "8080"
  - name: "catalog"
    value: "tpch"
  - name: "schema"
    value: "sf1"
  - name: "user"
    value: "iesi"
  - name: "password"
    value: ""
```


