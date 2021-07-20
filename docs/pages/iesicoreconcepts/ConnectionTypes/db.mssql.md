{% include navigation.html %}
# db.mssql
## Purpose
This type connects to a Microsoft SQL Server database ([https://www.microsoft.com/nl-nl/sql-server/](https://www.microsoft.com/nl-nl/sql-server/)).

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
  name: "db.mssql.1"
  type: "db.mssql"
  description: "db.mssql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "1433"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "sa"
  - name: "password"
    value: "ENC(Sayhy3uBuDuXQdMY72NNgfdavP94U+Jk2HqEymnpYG9kjvCAvg==)"
```
