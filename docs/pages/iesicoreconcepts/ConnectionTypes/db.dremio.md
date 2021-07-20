{% include navigation.html %}
# db.dremio
## Purpose
This connection is for Dremio Data-as-a-Service platform (https://www.dremio.com)

*Use Cases*
* SQL query engine abstraction
* Big data testing

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is located|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|
|mode|The connection mode to use: direct, zookeeper|string|Y|N|        
|cluster|The optional cluster name if connection mode is zookeeper|string|N|N|
|schema|The schema name in the catalog to connect to|string|N|N|
|user|The user name to connect to the database|string|Y|N|       
|password|The encrypted password to connect to the database|string|N|Y|  
     
## Example 
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
