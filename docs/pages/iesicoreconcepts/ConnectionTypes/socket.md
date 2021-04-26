{% include navigation.html %}
# socket
## Purpose
Socket Connection

*Use cases*
* Connect to socket

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|hostname|The host name|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|        


## Example
```yaml
---
type: Connection
data:
  name: "socket.1"
  type: "socket"
  description: "socket.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "2222"
```
