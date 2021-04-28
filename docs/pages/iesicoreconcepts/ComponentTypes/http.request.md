{% include navigation.html %}

# http.request

This component type is used to call http or rest services

## Use cases

* API Testing

## Fields

* **type**: http.request
* **name**: component reference name
* **version**: version of the component
* **description**: description of the component
* **parameters**:
  * endpoint: mandatory, string, endpoint of the api
  * type: mandatory, string, type of API call [GET, POST,..]
  * connection: mandatory, string, reference name of connection
  * headers: optional, list of strings, headers to be included in the API call
  * queryParameters: optional, list of strings, query parameters to be included in the url

## Examples

```yaml
---
type: Component
data:
  type: "http.request"
  name: "add.new.pet"
  version: "1"
  description: "add a new pet to the store"
  parameters:
  - name: "endpoint"
    value: "/pet"
  - name: "type"
    value: "POST"
  - name: "connection"
    value: "pets"
  - name: "headers"
    value: ""
  - name: "queryParameters"
    value: ""
```
