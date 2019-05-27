{% include navigation.html %}

# db.sqlite

This type connects to a Sqlite database.

Get more information at [https://www.sqlite.org](https://www.sqlite.org).

## Use cases

* Connect to database
* Use a Sqlite database for storing data locally

## Parameters

### 1: filePath

`filePath: "filePath"`
* define the file path where the database file is stored

### 2: fileName

`fileName: "fileName"`
* define the file name of the database file

## Examples

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
