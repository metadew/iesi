# fwk.alias
## Purpose
This connection is virtual and acts as a placeholder. It needs to be used together with the impersonation concept. 
In this case the alias is substituted at runtime with the connection that impersonates it. 

*Use cases*
* Write scipts that are independent of a connection
* Write script across distributed data centers and cloud environments


## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|type|Connection alias only to be used for impersonation|string|Y|N|

## Example
```yaml
---
type: Connection
data:
  name: "fwk.alias.1"
  type: "fwk.alias"
  description: "fwk.alias connection for host.linux"
  environment: "iesi-test"
  parameters:
  - name: "type"
    value: "host.linux"
```
