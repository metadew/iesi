{% include navigation.html %}

# http.host

This connection is for using http connections.

## Use cases

* Connect to http hosts

## Parameters

### 1: url

`url: "connection url"`
* define the connection url

## Examples

```yaml
---
type: Connection
data:
  name: "http.host.1"
  type: "http.host"
  description: "http.host.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "url"
    value: "https://localhost:3000/api"
```
