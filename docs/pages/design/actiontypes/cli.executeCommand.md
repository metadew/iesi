{% include navigation.html %}

# cli.executeCommand

This action executes a command on a host. 
* If the connection is a remote host, a ssh connection is established
* For Windows hosts, only local executions are supported by the framework itself

## Use cases

* Execution of a data processing flow
* Copy / move of files
* Verify if a file exists (using *ls* command)

## Parameters

|Parameter|Description|Options|Example|
|---|---|---|---|
|path|Path where to run the command from||/path/scripts|
|command|Command to run|/path/scripts/command.sh|
|connection|Connection where to run the shell command||myHost|
|setRuntimeVariables|Flag indicating if environment variables will be set as a runtime variable|experimental||
|setRuntimeVariablesPrefix|Prefix that will be used when retrieving or setting the variables|experimental||
|setRuntimeVariablesMode|Mode used to retrieve or set the variables|experimental||

## Examples

