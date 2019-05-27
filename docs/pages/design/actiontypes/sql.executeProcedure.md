{% include navigation.html %}

# sql.executeProcedure

This action executes a sql stored procedure on a relational database. 
The input parameters can be provided as one of the parameters. 
Output values can captured after select statement execution if required.

## Use cases

* Execute a sql stored procedure that is defined as api to the solution

## Parameters

### 1: procedure

`procedure: "any procedure to execute"`
* executes any procedure
* insert and delete queries are typically being used for unattended operations on the database.

### 2: parameters

`parameters: "parameters for the procedure"`
* parameters for the procedure
* different parameters are delimited by a comma `,` and written as `@Parameter=value`
* parameter names and syntax are required to be in line with the format of the database type where the procedure is executed on

### 3: connection

`connection: "connection where to execute the procedure"`
* name of the connection where to execute the procedure

### 4: outputDataset

`outputDataset: "dataset reference name"`
* name of the dataset where to store the output to
* this parameters only needs to be used in combination with a select statement that returns data at the end of the stored procedure

### 5: appendOutput

`appendOutput: "Y" / "N"`
* flag to indicate if the output needs to be overwritten

## Examples

```yaml
  - number: 1
    type: "sql.executeProcedure"
    name: "Action1"
    description: "Execute a procedure to return all data"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "procedure"
      value : "GetTable1All"
    - name: "connection"
      value : "db.mssql.1"
```

```yaml
  - number: 1
    type: "sql.executeProcedure"
    name: "Action1"
    description: "Execute a procedure to return data for a specific Field1 value"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "procedure"
      value : "GetTable1ByField1"
    - name: "connection"
      value : "db.mssql.1"
    - name: "parameters"
      value : "@Field1=1"
```
