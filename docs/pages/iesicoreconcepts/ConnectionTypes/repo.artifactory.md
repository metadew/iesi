{% include navigation.html %}
# repo.artifcatory
This connection is for artifactory repository connections ([https://jfrog.com/artifactory/](https://jfrog.com/artifactory/)).

*Use cases*
* Connect to an artifact repository


## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|url|The connection URL for the instance|string|Y|N|        
|user|The user name to connect to the database|string|Y|N|
|password|The encrypted password to connect to the database|string|Y|Y|
|repository|The repository name within the instance to connect to|string|N|N|

## Example
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
