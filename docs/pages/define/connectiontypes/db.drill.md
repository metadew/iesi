{% include navigation.html %}

# db.drill

This connection is for Apache Drill schema free sql query engine.

Get more information at [https://drill.apache.org/](https://drill.apache.org/).

## Use cases

* SQL query engine abstraction
* Big data testing

## Parameters

### 1: mode

`mode: "connection mode"`
* define the connection mode to use: drillbit, zookeeper

### 2: cluster

`cluster: "cluster names"`
* define the drillbit or zookeeper nodes including optional ports: node[:port],node[:port],etc

### 3: directory

`directory: "directory name"`
* define the optional drill directory in zookeeper which by default is drill

### 4: clusterId

`clusterId: "cluster id"`
* define the optional cluster id which by default is drillbits1

### 5: schema

`schema: "schema name"`
* define the optional name of a storage plugin configuration to use as the default for queries

### 6: tries

`tries: "tries parameter"`
* define the optional tries parameter for drillbit connections

## Examples

```yaml
---
type: Connection
data:
  name: "db.drill.1"
  type: "db.drill"
  description: "db.drill connection"
  environment: "iesi-test"
  parameters:
  - name: "mode"
    value: "drillbit"
  - name: "cluster"
    value: "localhost"
```
