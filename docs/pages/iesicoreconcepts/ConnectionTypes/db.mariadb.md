{% include navigation.html %}
# db.mariadb
## Purpose
This type connects to a Maria DB database (https://mariadb.org).

*Use cases*
* Connect to database

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
  name: "db.mariadb.1"
  type: "db.mariadb"
  description: "db.mariadb connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "3306"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```
