{% include navigation.html %}
# http
## Purpose
Connection to a server exposing HTTP endpoints

*Use cases*
* Connect to http hosts

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|port|The port number for connecting to the host|number|N|N|        
|baseUrl|All endpoints hosted at the host start with this specific endpoint|string|Y|N|
|tls|Whether or not the connection to the host is encrypted using TLS|string|Y|N|


## Example
```yaml
---
type: Connection
data:
  name: "http.1"
  type: "http"
  description: "http connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "2222"
  - name: "tls"
    value: "N"
  - name: "baseUrl"
    value: "/urlexample/"
```
