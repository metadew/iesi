# db.oracle
## Purpose
Oracle Database Connection

*Use Cases*
* Connect to database

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|
|tnsalias|The TfalseS Alias name to connect to the database|string|N|N|        
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|Y|Y|
|service|The Service name to connect to the database|string|N|N|        
          
## Example
```yaml
---
type: Connection
data:
  name: "db.oracle.1"
  type: "db.oracle"
  description: "db.oracle connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "3306"
  - name: "tnsalias"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(XXXXXXX)"
  - name: "service"
    value: ""
```
