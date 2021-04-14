# host.windows
## Purpose
This connection is for windows based host systems. Remote connections are not possible. This is only used when running the framework on a windows machine.

*Use cases*
* Run the framework on a windows machine


## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|host|The host name of the server where the database is running|string|Y|N|
|tempPath|The temporary path on the host name that can be used by the solution|string|N|N|

## Example
```yaml
---
type: Connection
data:
  name: "host.windows.1"
  type: "host.windows"
  description: "host.windows.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "tempPath"
    value: "#iesi.home#/run/tmp"
```
