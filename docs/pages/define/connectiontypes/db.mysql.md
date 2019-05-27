{% include navigation.html %}

# db.mysql

This type connects to a MySQL DB database.

Get more information at [https://www.mysql.com/](https://www.mysql.com/).

## Use cases

* Connect to database

## Parameters

### 1: host

`host: "host name"`
* define the host name of the server where the database is running

### 2: port

`port: "port number"`
* define the port number for connecting to the host

### 3: schema

`schema: "schema name"`
* define the schema name to connect to

### 4: user

`user: "user name"`
* define the user name to use for the establishing the connection

### 5: password

`password: "user password"`
* define the user password to use for establishing the connection

## Examples

```yaml
---
type: Connection
data:
  name: "db.mysql.1"
  type: "db.mysql"
  description: "db.mysql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "3306"
  - name: "schema"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```