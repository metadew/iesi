
{% include navigation.html %}
# db.drill
## Purpose
This connection is for Apache Drill schema free sql query engine (https://drill.apache.org)

*Use Cases*
* SQL query engine abstraction
* Big data testing

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|mode|The connection mode to use: drillbit, zookeeper|string|Y|N|
|cluster|The drillbit or zookeeper nodes including optional ports: node[:port],node[:port],etc'|string|Y|N|
|directory| Optional drill directory in zookeeper which by default is drill|string|N|N|        
|clusterId|Optional cluster Id which by default is drillbits1|string|N|N|
|schema|Optional name of a storage plugin configuration to use as the default|string|N|N|
|tries|Optional tries parameter for drillbit connections|string|N|N|     


## Example

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
