{% include navigation.html %}

# db.mssql

This type connects to a Microsoft SQL Server database.

Get more information at [https://www.microsoft.com/nl-nl/sql-server/](https://www.microsoft.com/nl-nl/sql-server/).

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
  name: "db.mssql.1"
  type: "db.mssql"
  description: "db.mssql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "1433"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "sa"
  - name: "password"
    value: "ENC(Sayhy3uBuDuXQdMY72NNgfdavP94U+Jk2HqEymnpYG9kjvCAvg==)"
```