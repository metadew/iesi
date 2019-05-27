{% include navigation.html %}

# db.netezza

This type connects to a Netezza database.

Get more information at [https://www.ibm.com/analytics/netezza](https://www.ibm.com/analytics/netezza).

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
  name: "db.netezza.1"
  type: "db.netezza"
  description: "db.netezza connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "5490"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```