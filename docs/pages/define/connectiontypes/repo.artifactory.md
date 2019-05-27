{% include navigation.html %}

# repo.artifactory

This connection is for artifactory repository connections.

Get more information at [https://jfrog.com/artifactory/](https://jfrog.com/artifactory/).

## Use cases

* Connect to an artefact repository

## Parameters

### 1: url

`url: "connection url"`
* define the connection url of the instance

### 2: repository

`repository: "repository name"`
* define the repository name within the instance to connect to

### 3: user

`user: "user name"`
* define the user name to use for the establishing the connection

### 4: password

`password: "user password"`
* define the user password to use for establishing the connection


## Examples

```yaml
---
type: Connection
data:
  name: "repo.artifactory.1"
  type: "repo.artifactory"
  description: "repo.artifactory.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "url"
    value: "https://localhost/artifactory"
  - name: "repository"
    value: "iesi"
  - name: "user"
    value: "iesi"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```


