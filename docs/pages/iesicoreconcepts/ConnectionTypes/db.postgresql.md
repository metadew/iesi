{% include navigation.html %}
# db.postgresql
## Purpose
This type connects to a Postgresql database ([https://www.postgresql.org/](https://www.postgresql.org/)).

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
  name: "db.postgresql.1"
  type: "db.postgresql"
  description: "db.postgresql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "5432"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "admin"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```
