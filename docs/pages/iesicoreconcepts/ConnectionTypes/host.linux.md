{% include navigation.html %}
# host.linux
## Purpose
This connection is for linux based host systems. Remote connections require ssh to be active.

*Use cases*
* Connect to remote hosts

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|port|The port number for connecting to the host|number|Y|N|        
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|Y|Y|
|tempPath|The temporary path on the host name that can be used by the solution|string|N|N|
|simulateTerminal|Flag indicating if a terminal needs to be simulated when connecting|string|N|N|
|jumphostConnections|Connection names that need to be used as jump host for connecting|string|N|N|
|allowLocalhostExecution|Flag indicating if processes are allowed to run as localhost on|string|N|N|

## Example
```yaml
---
type: Connection
data:
  name: "host.linux.1"
  type: "host.linux"
  description: "host.linux.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "2222"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
  - name: "tempPath"
    value: "/tmp"
  - name: "simulateTerminal"
    value: "N"
```
