{% include navigation.html %}
# db.netezza
## Purpose
This type connects to a Netezza database ([https://www.ibm.com/analytics/netezza](https://www.ibm.com/analytics/netezza)).

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
  name: "db.netezza.1"
  type: "db.netezza"
  description: "db.netezza connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "5490"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```
