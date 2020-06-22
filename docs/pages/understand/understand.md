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
|RUNNING|Script is running|
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

The framework uses configuration to drive automation. Automation engineers can create or update configuration files that will be loaded and stored in a configuration repository. 
There are multiple ways to create these files: waiting for a more advance graphical user (web)interface, the framework contains a set of standard Excel templates. 
These provide a well-known user interface and facilitate creating a required technical `json` format. Engineers can directly create this technical format, 
or alternatively make use of a easier to use `yaml` format.

### Creating configuration files

**Json format**

Underneath, the framework uses the `json` object format run and communicate its services.

**Yaml format**

To facilitate creating and updating configuration files for technical enigeers, support for the `yaml` object format has been included.

**Excel templates**

The templates allow to transfer the configuration Excel files into a `json` configuration file via a `vba` macro. 
The configuration templates can be found in folder `modules/templates`.

|Template|Description|
|--------|-----------|
|Component|Define a library of system components|
|Connectivity|Define connections and environments|
|Script|Design automation scripts|
|Subroutine|Design subroutines|

### Versioning configuration files

Versioning can be done via any version tool that is being used.

## Configuration repository

All tables of the framework are installed in a specific area of the database. 
Depending on the database type (e.g. Oracle, Postgresql, SQLite, ...), this area can be either a database or schema. 
To support this multi-database support all table configurations are stored by the framework in json format in the `metadata/def` folder. 

### Categories

The configuration data can be divided in different categories.

|Category|Description|
|--------|-----------|
|Connectivity|Connectivity configuration to automate actions. This can include parameters that are resolved during execution.|
|Design|Automation configuration as designed by the automation engineer. This can include parameters and other reusable constructs that are resolved during execution.|
|Execution|In-process information to drive executions.|
|Result|The technical outcome for the different actions as executed by the framework.|
|Trace|The resolution of the automation configuration design as it has been executed by the framework. All parameters and reusable constructs are replaced by actual values.|

### Data Objects

The framework is based on a set of data objects that work closely together. All objects are designed using the following principle:
* Object relates here to a primary object, a primary object is an object which is meaningful both from a functional and technical point of view and will be used by the framework for its services
* An object has a natural identifer (e.g. a name) for external reference (including between different primary objects; internally, a unique identifier will be used
* An object is of a *type*
* An object type has *paramters*
* An object has *parameters*, in line with the object type parameters
* If applicable, an object has a version
* An object has fields and can contain other objects (these are always secondary objects), secondary objects 

*You can replace object here with: script, environment, connection, etc.*

### Users

The framework is built around 3 users having access to the configuration repository:

|User type|Description|
|---------|-----------|
|Owner|For creating tables and to be used as logon for deployments|
|Writer|For processes writing data to the tables|
|Reader|A read-only account for reference / reporting purposes|

### Tables

* Table name structure: IESI_[instance]_[prefix]_[name]
* The instance part is available to allow multiple logical instances to run inside a single physical instance. The instance part is defined in the metadata repository configuration file.
* The prefix is specific for a given type of configuration data. It allows to group tables based on this configuration data type.
* Detailed tables (e.g. parameters) reuse the table name that they extend adding a specific suffix (e.g. _PAR)

Prefixes:

|Prefix|Description|
|------|-----------|
|CXN|Connectivity tables|
|DES|Design tables|
|EXE|Execution tables|
|RES|Result tables|
|TRC|Trace tables|

### Data Model

The data model for the solution is stored in the `metadata/def` folder. To support multi-database support the structure is stored in a data store agnostic `json` file. 
For each category a file `[Category]Tables.json` can be found here. The file `[Category]Objects.json` contains the relevant object definitions for the category. 
More information on the data models for the different categories can be found by clicking on the appropriate link below:

* [connectivity](/{{site.repository}}/pages/understand/datamodel/connectivity.html)
* [design](/{{site.repository}}/pages/understand/datamodel/design.html)
* [execution](/{{site.repository}}/pages/understand/datamodel/execution.html)
* [result](/{{site.repository}}/pages/understand/datamodel/result.html)
* [trace](/{{site.repository}}/pages/understand/datamodel/trace.html)

## Processing engine

The installation of the automation framework deploys a set of artefacts that allow the processing engine to initialize itself and perform its operations.

### Execution logic

The automation framework is an orchestrator that runs actions one after the other, caches intermediate results and variables that it can use in any next action.

Very simplisticly, it is a big loop over all actions:

* Start the execution
* For each action:
  * Verify if the action needs to be executed or skipped
  * Verify if the action needs to be iterated
  * Execute the action (once or for every execution)
    * If a condition has been specified, is it valid? If so, execute the action; otherwise, do not execute the action
    * If [error expected](/{{site.repository}}/pages/design/expectederrors.html) is relevant, the result of the action execute is reversed
  * If the action result is an error:
    * Is [stop on error](/{{site.repository}}/pages/design/stoponerror.html) relevant? If so, stop the execution
    * Has a retry number been specified? If so, re-execute the action
* End the execution

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
  * At the end of the execution, the variables are no longer available
* There are some restrictions:
  * A variable name can never be the same as a configuration setting name. 
  The framework will always keep the value of the configuration setting.
