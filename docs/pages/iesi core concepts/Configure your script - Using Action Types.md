# Configure your script - Using Action Types
## What are Action Types?
Action types are the **reusable building blocks** of automation scripts. **Each type performs a specific operation and requires a different set of input parameters**.

Depending on the type of operation, the framework has embedded the notion of action types. During the design of the script, the automation engineer can make use of these types to accelerate the design of his/her script.

Goal is to compose the script using the correct ‘building blocks’ and eventually result in a set of scripted actions. 

![Action Types Design](docs/images/introduction/action_types_concept.png)

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
|      |cli.executeCommand|Run a Shell Command|
|**conn**|
|      |conn.isAlive|Verify if the connection can be reached|
|      |conn.setStageConnection|Define a stage connection that can be used in any next step|
|**data**|
|      |data.compareDataset|Compare two datasets|
|      |data.outputDataset|Prints a dataset for logging and debugging purposes|
|      |data.setDatasetConnection|Define a dataset connection that can be used in any next action|
|**ddl**|
|      |ddl.generateFromFile|Generates a ddl from a framework file structure|
|**eval**|
|      |eval.assertEquals|Evaluate if two values are equal|
|      |eval.listContains|Evaluate if list contains specific values|
|      |eval.executeExpression|Evaluate an expression using scripted syntax|
|      |eval.verifyMandatoryField|Evaluate if mandatory fields are filled out|
|      |eval.verifySingleField|Check a single field against a specific rule|
|**fho**|
|      |fho.createFolder|Create a folder|
|      |fho.deleteFile|Delete one or more files in a folder|
|      |fho.deleteFolder|Delete one or more folders and all contents|
|      |fho.executeFileTransfer|Transfer one or more files between locations|
|      |fho.fileExists|Verify if a file exists|
|      |fho.folderExists|Verify if a folder exists|
|**fwk**|
|      |fwk.dummy|Dummy for adding a placeholder step|
|      |fwk.executeScript|Execute another script|
|      |fwk.executeSuite|Execute a test suite of multiple scripts|
|      |fwk.exitScript|Exit the script execution|
|      |fwk.includeScript|Include the actions of another script and execute as one script|
|      |fwk.outputMessage|Prints a message for logging and debugging purposes|
|      |fwk.route|Route to one or more specific actions|
|      |fwk.setEnvironment|Set the environment where the next steps will be executed on|
|      |fwk.setIteration|Define an interation that can be used in any next action|
|      |fwk.setParameterFile|Load a parameter file as runtime variables|
|      |fwk.setParameterList|Load a list of parameters as runtime variables|
|      |fwk.setParameterValue|Set a parameter value as runtime variable|
|      |fwk.setRepository|Define a repository that can be used in any next action|
|      |fwk.startIteration|Start an iteration block for the steps|
|      |fwk.stopIteration|Stop the iteration block|
|**http**|
|      |http.executeRequest|Execute a http request|
|**java**|
|      |java.parseJar|Parses a Java archive file|
|**mod**|
|      |mod.soapui|Run a SOAP UI test scope|
|**script**|
|      |script.logOutput|Store an output value as part of the script results|
|**socket**|
|      |socket.transmitMessage|Execute a socket message transfer|
|**sql**|
|      |sql.evaluateResult|Checks if a SQL query returns a result or not|
|      |sql.executeProcedure|Execute a SQL Stored Procedure|
|      |sql.executeQuery|Run a SQL query|
|      |sql.executeStatement|Run a SQL statement (not returning any result)|
|      |sql.setIterationVariables|Retrieve iteration variables using a SQL Statement|
|      |sql.setRuntimeVariables|Retrieve runtime variables using a SQL Statement|
|**wfa**|
|      |wfa.executeFilePing|Wait until a File Statement returns a result or not|
|      |wfa.executeQueryPing|Wait until a SQL query returns a result or not|
|      |wfa.executeWait|Wait for a defined interval|
|      |wfa.getConfirmation|Wait for user confirmation|
