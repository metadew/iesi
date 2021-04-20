# db.sqlite
## Purpose
This type connects to a Sqlite database ([https://www.sqlite.org](https://www.sqlite.org)).

*Use cases*
* Connect to database
* Use a Sqlite database for storing data locally

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|filePath|The file path where the database is stored|string|Y|N|
|filefalseame|The file name of the the database|string|Y|N|

## Example
```yaml
---
type: Connection
data:
  name: "sql.evaluateResult.1"
  type: "db.sqlite"
  description: "sql.evaluateResult.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "filePath"
    value: "#iesi.home#/data/iesi-test/fwk/data/actions"
  - name: "fileName"
    value: "sql.evaluateResult.1.db3"
```
