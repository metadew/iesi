# db.presto
## Purpose
This connection is for Presto Distributed SQL Query Engine ([http://prestodb.github.io](http://prestodb.github.io)).

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database file is located|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|
|catalog|The catalog name to connect to|string|N|N|        
|schema|The schema name in the catalog to connect to|string|N|N|
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|N|Y|
    
## Example
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

