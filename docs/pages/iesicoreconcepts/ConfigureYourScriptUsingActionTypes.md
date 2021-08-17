{% include navigation.html %}
# Configure your script - Using Action Types
## What are Action Types?
Action types are the **reusable building blocks** of automation scripts. **Each type performs a specific operation and requires a different set of input parameters**.

Depending on the type of operation, the framework has embedded the notion of action types. During the design of the script, the automation engineer can make use of these types to accelerate the design of his/her script.

Goal is to compose the script using the correct ‘building blocks’ and eventually result in a set of scripted actions. 

![Action Types Design](/{{site.repository}}/images/introduction/action_types_concept.png)

## Action Types - Overview
The action types are prefix based on the **actions category**:

|Prefix|Category|
|---|---|
|action|Action related operations|
|cli|Command line instructions|
|conn|Connectivity operations|
|data|Data related operations|
|ddl|DDL related operations|
|eval|Evaluation operations|
|fho|File handling operations|
|fwk|Framework operations|
|http|Http-based operations|
|java|Java related operations|
|script|Script operations|
|socket|Socket related operations|
|sql|Database SQL operations|
|wfa|Wait for activity operations|

Each category contains one or multiple **action types**:

|Category|Type|Description            |
|--------|----|-----------------------|
|**action**|
|      |action.execute|Re-execute an action that is already defined in the script|
|**cli**|
|      |[cli.executeCommand](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/cli.executeCommand.html)|Run a Shell Command|
|**conn**|
|      |conn.isAlive|Verify if the connection can be reached|
|      |conn.setStageConnection|Define a stage connection that can be used in any next step|
|**data**|
|      |data.compareDataset|Compare two datasets|
|      |data.outputDataset|Prints a dataset for logging and debugging purposes|
|      |[data.setDatasetConnection](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/data.setDatasetConnection.html)|Define a dataset connection that can be used in any next action|
|**ddl**|
|      |ddl.generateFromFile|Generates a ddl from a framework file structure|
|**eval**|
|      |[eval.assertEquals](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/eval.assertEquals.html)|Evaluate if two values are equal|
|      |[eval.listContains](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/eval.listContains.html)|Evaluate if list contains specific values|
|      |[eval.executeExpression](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/eval.executeExpression.html)|Evaluate an expression using scripted syntax|
|      |eval.verifyMandatoryField|Evaluate if mandatory fields are filled out|
|      |eval.verifySingleField|Check a single field against a specific rule|
|**fho**|
|      |[fho.createFolder](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.createFolder.html)|Create a folder|
|      |[fho.deleteFile](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.deleteFile.html)|Delete one or more files in a folder|
|      |[fho.deleteFolder](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.deleteFolder.html)|Delete one or more folders and all contents|
|      |[fho.executeFileTransfer](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.executeFileTransfer.html)|Transfer one or more files between locations|
|      |[fho.fileExists](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.fileExists.html)|Verify if a file exists|
|      |[fho.folderExists](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fho.folderExists.html)|Verify if a folder exists|
|**fwk**|
|      |[fwk.dummy](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.dummy.html)|Dummy for adding a placeholder step|
|      |[fwk.executeScript](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.executeScript.html)|Execute another script|
|      |fwk.executeSuite|Execute a test suite of multiple scripts|
|      |fwk.exitScript|Exit the script execution|
|      |[fwk.includeScript](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.includeScript.html)|Include the actions of another script and execute as one script|
|      |[fwk.outputMessage](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.outputMessage.html)|Prints a message for logging and debugging purposes|
|      |fwk.route|Route to one or more specific actions|
|      |[fwk.setEnvironment](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.setEnvironment.html)|Set the environment where the next steps will be executed on|
|      |[fwk.setIteration](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.setIteration.html)|Define an interation that can be used in any next action|
|      |[fwk.setParameterFile](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.setParameterFile.html)|Load a parameter file as runtime variables|
|      |[fwk.setParameterList](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.setParameterList.html)|Load a list of parameters as runtime variables|
|      |[fwk.setParameterValue](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/fwk.setParameterValue.html)|Set a parameter value as runtime variable|
|      |fwk.setRepository|Define a repository that can be used in any next action|
|      |fwk.startIteration|Start an iteration block for the steps|
|      |fwk.stopIteration|Stop the iteration block|
|**http**|
|      |[http.executeRequest](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/http.executeRequest.html)|Execute a http request|
|**java**|
|      |java.parseJar|Parses a Java archive file|
|**mod**|
|      |mod.soapui|Run a SOAP UI test scope|
|**script**|
|      |[script.logOutput](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/script.logOutput.html)|Store an output value as part of the script results|
|**socket**|
|      |[socket.transmitMessage](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/socket.transmitMessage.html)|Execute a socket message transfer|
|**sql**|
|      |[sql.evaluateResult](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/sql.evaluateResult.html)|Checks if a SQL query returns a result or not|
|      |[sql.executeProcedure](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/sql.executeProcedure.html)|Execute a SQL Stored Procedure|
|      |[sql.executeQuery](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/sql.executeQuery.html)|Run a SQL query|
|      |[sql.executeStatement](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/sql.executeStatement.html)|Run a SQL statement (not returning any result)|
|      |sql.setIterationVariables|Retrieve iteration variables using a SQL Statement|
|      |[sql.setRuntimeVariables](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/sql.setRuntimeVariables.html)|Retrieve runtime variables using a SQL Statement|
|**wfa**|
|      |[wfa.executeFilePing](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/wfa.executeFilePing.html)|Wait until a File Statement returns a result or not|
|      |[wfa.executeQueryPing](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/wfa.executeQueryPing.html)|Wait until a SQL query returns a result or not|
|      |[wfa.executeWait](/{{site.repository}}/pages/iesicoreconcepts/ActionTypes/wfa.executeWait.html)|Wait for a defined interval|
|      |wfa.getConfirmation|Wait for user confirmation|
