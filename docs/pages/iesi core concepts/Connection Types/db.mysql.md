# db.mysql
## Purpose
This type connects to a MySQL DB database ([https://www.mysql.com/](https://www.mysql.com/)).

*Use cases*
* Connect to database

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|
|schema|The schema name to connect to the database|string|Y|N|        
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|Y|Y|

## Example
```yaml
---
type: Connection
data:
  name: "db.mysql.1"
  type: "db.mysql"
  description: "db.mysql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "3306"
  - name: "schema"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```
