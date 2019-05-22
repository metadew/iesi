{% include navigation.html %}

# fwk.alias

This connection is virtual and acts as a placeholder. It needs to be used together with the impersonation concept. 
In this case the alias is substituted at runtime with the connection that impersonates it. 

## Use cases

* Write scipts that are independent of a connection
* Write script across distributed data centers and cloud environments

## Parameters

### 1: type

`type: "connection type"`
* define the connection type for the alias

## Examples

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
