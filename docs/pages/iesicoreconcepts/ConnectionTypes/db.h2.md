{% include navigation.html %}
# db.h2
## Purpose
This type connects to a H2 database (https://www.h2database.com).

*Use cases*
* Connect to database
* Use a H2 database for storing data locally
* Share data across local installation using H2's server mode

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database file is located|string|N|N|
|type|The type of h2 database (embedded/server/memory)|number|N|N|
|name|The name of the h2 database (memory)|string|N|N|        
|port|The port number for connecting to the host|number|N|N|
|path|The path where the database is stored|string|N|N|
|file|The database file name to connect to|string|Y|N|      
|user|The user name to connect to the database|string|N|N| 
|password|The encrypted password to connect to the database|string|N|Y|  

## Example
```yaml
---
type: Connection
data:
  name: "db.h2.1"
  type: "db.h2"
  description: "db.h2 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: ""
  - name: "port"
    value: ""
  - name: "path"
    value: "#iesi.home#/data/iesi-test/fwk/data/connections"
  - name: "file"
    value: "db.h2.1"
  - name: "user"
    value: ""
  - name: "password"
    value: ""
```
