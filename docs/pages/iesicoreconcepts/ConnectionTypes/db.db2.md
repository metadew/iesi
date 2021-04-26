{% include navigation.html %}
# db.db2
## Purpose
IBM DB2 Database Connection

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|
|database|The database name to connect to|string|Y|N|        
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|Y|Y|
       

## Example
```yaml
---
type: Connection
data:
  name: "db.db2.1"
  type: "db.db2"
  description: "db.db2 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: ""
  - name: "port"
    value: ""
  - name: "database"
    value: "path/to/database/"
  - name: "user"
    value: "XXXXX"
  - name: "password"
    value: "ENC(XXXXXXX)"
```
