{% include navigation.html %}

# host.windows

This connection is for windows based host systems. Remote connections are not possible. This is only used when running the framework on a windows machine.

## Use cases

* Run the framework on a windows machine

## Parameters

### 1: host

`host: "host name"`
* define the host name of the machine
* this can be the logical name or the ip address

### 2: tempPath

`tempPath: "temporary work directory"`
* define a temporary directoy path that can be used by the framework
* the user define above needs to have read, write and execute rights on this directory

## Examples

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


