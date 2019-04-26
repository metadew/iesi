{% include navigation.html %}

# Expected errors

Setting the expected error flag to `Y` allows to define behaviour of the framework for negative testing. 
* Positive tests verify if the system is working as expected
* Negative tests verify what happens if the system or data is not as expected
* Negative tests are tests that are a success if they fail and are captured correctly

If the flag is set to `Y` then an error in the execution is considered as a successful execution. 
The result interpretation of the action is reversed. 
Below are some examples where the expected error flag has been set to `Y`

|Action type|Command|Output|System result|Framework result|
|---|---|---|---|---|
|cli.executeCommand|ls /path/file.ext|Cannot find file|$?!= 0|Success|
|cli.executeCommand|ls /path/file.ext|File found (stats)|$?== 0|Error|
|sql.evaluateResult|select * from table where exp=1|No records found|count = 0|error|
|sql.evaluateResult|select * from table where exp=1|100 records found|count = 0|success|
