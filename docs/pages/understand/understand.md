{% include navigation.html %}

# Understand the automation framework

## Concept

The framework starts from the configuration of automation scripts that will be executed by a processing engine. 
The configuration is designed upfront and loaded in a configuration reposiory. 
From here, the engine will read this configuration, execute it and store its outcome back into the repository. 
The framework will take care of common and reusable functions such as logging, parameter passing, etc.

## Principles

* Scripts are designed to be environment independent
* Therefore, a script is always executed on a specific environment
* Each execution of a script
  * is associated with a unique run identifier
  * is logged with start, end and status information
* Each execution of an action
  * is associated with a unique process identifier
* Variables allow automation scripts to be designed in a generic manner where execution depends on user input or execution context

## Different Statuses

The execution of an automation script can be associated with the following statuses.

|Status|Description|
|------|-----------|
|ACTIVE|Script is running|
|ERROR|All actions in the script have ended in error|
|STOPPED|Script has stoppped due to an error in an action that is configured to stop on error|
|SUCCESS|All actions in the script have been executed successfully|
|WARNING|Not all actions in the script have been executed successfully, at least one action has ended in error|
  
# The automation framework components

The automation framework consists of:
* a set of Excel templates to define and design automation solutions
* a configuration repository where all designs and results are stores
* a processing engine to operate automation solutions

## Configuration templates

The framework contains a set of Excel templates to create several configuration assets so that they can be loaded into the configuration repository. 
Automation engineers can create new configuration Excel files via a well-known user interface and use any version control tool that they are used to. 
The templates allow to transfer the configuration Excel files into a `json` configuration file via a `vba` macro. 
The configuration templates can be found in folder `modules/templates`.

> The Excel template files are only temporary waiting for a more advanced graphical user (web)interface. 

|Template|Description|
|--------|-----------|
|Component|Define a library of system components|
|Connectivity|Define connections and environments|
|Script|Design automation scripts|
|Subroutine|Design subroutines|

## Configuration repository

All tables of the framework are installed in a specific area of the database. 
Depending on the database type (e.g. Oracle, Postgresql, SQLite, ...), this area can be either a database or schema. 
To support this multi-database support all table configurations are stored by the framework in json format in the `metadata/def` folder. 

### Categories

The configuration data can be divided in different categories.

|Category|Description|
|--------|-----------|
|Design|Automation configuration as designed by the automation engineer. This can include parameters and other reusable constructs that are resolved during execution.|
|Connectivity|Connectivity configuration to automate actions. This can include parameters that are resolved during execution.|
|Trace|The resolution of the automation configuration design as it has been executed by the framework. All parameters and reusable constructs are replaced by actual values.|
|Result|The technical outcome for the different actions as executed by the framework.|
|Reporting|The interpretation of the technical outcome using reporting views to give context to the execution.|



### Users

The framework is built around 3 users having access  to the configuration repository:

|User type|Description|
|---------|-----------|
|Owner|For creating tables and to be used as logon for deployments|
|Writer|For processes writing data to the tables|
|Reader|A read-only account for reference / reporting purposes|

### Tables

* Table name structure: IESI_[instance]_[name]
* The instance part is available to allow multiple logical instances to run inside a single physical instance
* Detailed tables (e.g. parameters) reuse the table name that they extend adding a specific suffix (e.g. _PAR)

Prefixes:

|Prefix|Description|
|------|-----------|
|||

We are working providing more details on this.

### Data Model

## Processing engine

We are working providing more details on this.

### Folder structure

We are working providing more details on this.

### File extensions

|Extension|Description|
|---------|-----------|
|conf|Configuration file|
|cmd|Windows cmd shell script|
|db3|SQLite database file|
|ini|Configuration file initialization|
|jar|Java archive file|
|json|json file|
|log|Log file|
|sh|Linux bash shell script|
|xml|XML file|
|yml|yaml file|

### Using variables

Variables allow automation scripts to be designed in a generic manner where execution depends on user input or execution context. 
* User input: a user can define one or more parameters when starting the script execuction. 
These parameters can be provided on the command line or via a specific file. 
More information can be found [here](/{{site.repository}}/pages/operate/operate.html).
* Execution context: variables can be stored during execution of an action and can be used afterwards. 
Which action stores what variable data depends on the action type used. 
More information can be found [here](/{{site.repository}}/pages/design/actiontypes.html).

Variables are always available as **key-value pairs**
* A variable is identified with the `#` symbol before and after the variable name: `#variable#`
* A variable is resolved during execution
* A variable has a name and a value `key=value`
* A variable name needs to be unique **for the highest level of execution**
  * This can be for: script
  * The variable name needs to be unique within the script execution context
  * If a same variable name is loaded in the execution context, the previous value is overwritten. 
  Therefore, naming the variables appropriately is very important.
  * Variables are loaded according to a specific hierarchy:
    * Parameter file
	* Parameter list
	* Action execution
  * At the end of the execution, the variables are no longer available
* There are some restrictions:
  * A variable name can never be the same as a configuration setting name. 
  The framework will always keep the value of the configuration setting.
  
**Variable resolution within an action**

Where the definition of variables is already providing a great means of writing generic scripts, 
variable resolution within an action is allowing to make a reference to any action parameter value. 
In this way, parameter values can easily be reused in the action without needing to duplicate their definition or logic. 
* A variable from another action parameter value is identified with the `[]` symbols: `[parameterName]`
* As a standard variable, this definition is reolved during execution, but after the standard variable resolution
* The variable resolution is only valid **for the action execution context**

**Variable resolution across actions**

The framework caches action details so that they can be reused in another action easily.

We are working providing more details on this.