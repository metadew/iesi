{% include navigation.html %}

# db.dremio

This connection is for Dremio Data-as-a-Service platform.

Get more information at [https://www.dremio.com/](https://www.dremio.com/).

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

### 3: mode

`mode: "connection mode"`
* define the connection mode to use: direct, zookeeper

### 4: cluster

`cluster: "cluster names"`
* define the cluster name for zookeeper
* this is optional and only needed when connection mode is zookeeper

### 5: schema

`schema: "schema name"`
* define the optional schema name in the catalog to connect to

### 6: user

`user: "user name"`
* define the user name to use for the establishing the connection

### 7: password

`password: "user password"`
* define the user password to use for establishing the connection

## Examples

```yaml
---
type: Connection
data:
  name: "db.dremio.1"
  type: "db.dremio"
  description: "db.dremio connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "31010"
  - name: "mode"
    value: "direct"
  - name: "schema"
    value: ""
  - name: "user"
    value: "iesi"
  - name: "password"
    value: "ENC(Waan0BqcryeQxFbWRhFtT0564xgrxWFOTA==)"
```


