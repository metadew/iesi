{% include navigation.html %}
# db.teradata
## Purpose
This type connects to a Teradata database ([https://www.teradata.com/](https://www.teradata.com/)).

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
  name: "db.teradata.1"
  type: "db.teradata"
  description: "db.teradata connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "1025"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "iesi"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```
