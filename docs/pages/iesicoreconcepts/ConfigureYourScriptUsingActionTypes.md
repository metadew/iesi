{% include navigation.html %}
# Configure your script - Using Action Types
## What are Action Types?
Action types are the **reusable building blocks** of automation scripts. **Each type performs a specific operation and requires a different set of input parameters**.

Depending on the type of operation, the framework has embedded the notion of action types. During the design of the script, the automation engineer can make use of these types to accelerate the design of his/her script.

Goal is to compose the script using the correct ‘building blocks’ and eventually result in a set of scripted actions. 

![Action Types Design](https://github.com/metadew/iesi/blob/f76e53b5cfc119f3ddb16c8857cec689146055f7/docs/images/introduction/action_types_concept.png)

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
|      |[cli.executeCommand](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/cli.executeCommand.md)|Run a Shell Command|
|**conn**|
|      |conn.isAlive|Verify if the connection can be reached|
|      |conn.setStageConnection|Define a stage connection that can be used in any next step|
|**data**|
|      |data.compareDataset|Compare two datasets|
|      |data.outputDataset|Prints a dataset for logging and debugging purposes|
|      |[data.setDatasetConnection](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/data.setDatasetConnection.md)|Define a dataset connection that can be used in any next action|
|**ddl**|
|      |ddl.generateFromFile|Generates a ddl from a framework file structure|
|**eval**|
|      |[eval.assertEquals](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/eval.assertEquals.md)|Evaluate if two values are equal|
|      |[eval.listContains](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/eval.listContains.md)|Evaluate if list contains specific values|
|      |[eval.executeExpression](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/eval.executeExpression.md)|Evaluate an expression using scripted syntax|
|      |eval.verifyMandatoryField|Evaluate if mandatory fields are filled out|
|      |eval.verifySingleField|Check a single field against a specific rule|
|**fho**|
|      |[fho.createFolder](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.createFolder.md)|Create a folder|
|      |[fho.deleteFile](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.deleteFile.md)|Delete one or more files in a folder|
|      |[fho.deleteFolder](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.deleteFolder.md)|Delete one or more folders and all contents|
|      |[fho.executeFileTransfer](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.executeFileTransfer.md)|Transfer one or more files between locations|
|      |[fho.fileExists](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.fileExists.md)|Verify if a file exists|
|      |[fho.folderExists](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fho.folderExists.md)|Verify if a folder exists|
|**fwk**|
|      |[fwk.dummy](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.dummy.md)|Dummy for adding a placeholder step|
|      |[fwk.executeScript](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.executeScript.md)|Execute another script|
|      |fwk.executeSuite|Execute a test suite of multiple scripts|
|      |fwk.exitScript|Exit the script execution|
|      |[fwk.includeScript](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.includeScript.md)|Include the actions of another script and execute as one script|
|      |[fwk.outputMessage](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.outputMessage.md)|Prints a message for logging and debugging purposes|
|      |fwk.route|Route to one or more specific actions|
|      |[fwk.setEnvironment](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.setEnvironment.md)|Set the environment where the next steps will be executed on|
|      |[fwk.setIteration](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.setIteration.md)|Define an interation that can be used in any next action|
|      |[fwk.setParameterFile](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.setParameterFile.md)|Load a parameter file as runtime variables|
|      |[fwk.setParameterList](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.setParameterList.md)|Load a list of parameters as runtime variables|
|      |[fwk.setParameterValue](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/fwk.setParameterValue.md)|Set a parameter value as runtime variable|
|      |fwk.setRepository|Define a repository that can be used in any next action|
|      |fwk.startIteration|Start an iteration block for the steps|
|      |fwk.stopIteration|Stop the iteration block|
|**http**|
|      |[http.executeRequest](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/http.executeRequest.md)|Execute a http request|
|**java**|
|      |java.parseJar|Parses a Java archive file|
|**mod**|
|      |mod.soapui|Run a SOAP UI test scope|
|**script**|
|      |[script.logOutput](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/script.logOutput.md)|Store an output value as part of the script results|
|**socket**|
|      |[socket.transmitMessage](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/socket.transmitMessage.md)|Execute a socket message transfer|
|**sql**|
|      |[sql.evaluateResult](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/sql.evaluateResult.md)|Checks if a SQL query returns a result or not|
|      |[sql.executeProcedure](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/sql.executeProcedure.md)|Execute a SQL Stored Procedure|
|      |[sql.executeQuery](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/sql.executeQuery.md)|Run a SQL query|
|      |[sql.executeStatement](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/sql.executeStatement.md)|Run a SQL statement (not returning any result)|
|      |sql.setIterationVariables|Retrieve iteration variables using a SQL Statement|
|      |[sql.setRuntimeVariables](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/sql.setRuntimeVariables.md)|Retrieve runtime variables using a SQL Statement|
|**wfa**|
|      |[wfa.executeFilePing](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/wfa.executeFilePing.md)|Wait until a File Statement returns a result or not|
|      |[wfa.executeQueryPing](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/wfa.executeQueryPing.md)|Wait until a SQL query returns a result or not|
|      |[wfa.executeWait](https://github.com/metadew/iesi/blob/212e5e91162d55eefe2b01a582e63426ec1f91d3/docs/pages/iesi%20core%20concepts/Action%20Types/wfa.executeWait.md)|Wait for a defined interval|
|      |wfa.getConfirmation|Wait for user confirmation|
