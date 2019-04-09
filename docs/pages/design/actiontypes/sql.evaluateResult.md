{% include navigation.html %}

# sql.evaluateResult

This action executes a sql statement on a relational database and evaluates if a result is returned or not. 
If a result is found (count > 0) then the evaluation is successful, otherwise it is considered as an error. 

## Use cases

* Verify if data is loaded in a table
* Verify if a table is empty
* Verify if a record has been added to a logging table
* By using scripts,
  * more complex statements are possible
  * files can be organized allowing reuse via parameters

## Parameters

|Parameter|Description|Options|Example|
|---|---|---|---|

## Examples

* Run a script

