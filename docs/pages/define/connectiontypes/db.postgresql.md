{% include navigation.html %}

# db.postgresql

This type connects to a Postgresql database.

Get more information at [https://www.postgresql.org/](https://www.postgresql.org/).

## Use cases

* Connect to database

## Parameters

### 1: host

`host: "host name"`
* define the host name of the server where the database is running

### 2: port

`port: "port number"`
* define the port number for connecting to the host

### 3: database

`database: "database name"`
* define the database name to connect to

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
  name: "db.postgresql.1"
  type: "db.postgresql"
  description: "db.postgresql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "5432"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "admin"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```