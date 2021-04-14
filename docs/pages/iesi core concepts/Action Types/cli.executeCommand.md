## cli.executeCommand
## Purpose
This action executes a command on a host. 
* If the connection is a remote host, a ssh connection is established
* For Windows hosts, only local executions are supported by the framework itself

*Use Cases*
* Execution of a data processing flow
* Copy / move of files
* Verify if a file exists (using *ls* command)


## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|path|Path where to run the command from|string|N|N|
|command|Command to run|string|Y|N|
|setRuntimeVariables|Flag indicating if environment variables will be set as a runtime|string|N|N|        
|setRuntimeVariablesPrefix|Prefix that will be used when retrieving or setting the variables|string|N|N|
|setRuntimeVariablesMode|Mode used to retrieve or set the variables|string|N|N|
|output|Will be used to store the system output|string|N|N|
|connection|Connection where to run the shell command|string|Y|N|

## Example
TBD
