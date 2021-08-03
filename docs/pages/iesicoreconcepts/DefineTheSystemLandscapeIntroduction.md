{% include navigation.html %}
# Define the system landscape - Introduction

## General
Before we are able to trigger any execution, it is required to first define the system landscape on which our script will be executed on. We need to analyse the following: On which systems will the actions be performed? How can the automation framework connect to these systems? …

Therefore, it is important to first define the connectivity end points. These configuration settings are **defined upfront** and will be **given logical names**. In that way, automation engineers will be efficient in designing and building their automation scripts while details are being retrieved during the execution.

Two configurations are crucial to get started:
*	**Environment configuration**: defines the number of environments (e.g. development, test, production)
* **Connection configuration**: defines the different systems that the framework can connect to, including required details such as user names, passwords, addresses, etc.

Next to that, we will also deep dive into working with:
*	**Component configuration**: register a connectivity option as a component for high reusability

## Environments
In an enterprise environment there is a clear distinction between development, test and production environments. In order to make the distinction between the different connectivity end points linked to any environment, each connection needs to be linked to one environment. When designing the automation script, the environment name is not added to the configuration. Rather, it is provided when starting the execution as one of the input parameters. As such, **the same automation script can be executed on any environment.**

Similar to connection configuration, logical names for the definition of environments are used. This means that it can be easily mapped regardless of the number or scope of instances.

### *Example*

Environment|Description
-----------|-------------
Dev|Development environment
Test|Test environment
Prod|Production environment
...|...

### *Configure Environments*
It is possible to configure the necessary environments or update them using the connectivity template.
  * Define the name and description for the environments in the *Environments* sheet
  * Define parameters for the different environments in the *EnvironmentParameters* sheet
      * Parameters are loaded at the start of any execution
      * Typically used for environment dependent configurations such as hostnames, environment configuration files, etc.
  
**Important**: The name of the environment needs to be unique as it is used an identifier in the configuration of the connections

## Connections
Next to environments, also connections need to be defined. Each system that the automation framework will connect to is configured for any relevant environment. Depending on the type of system, the framework has embedded the notion of connection types. When defining the connection settings, the automation engineer can make use of these to quickly establish connectivity.

Connection types are **reusable configuration blocks** for which each type **allows the framework to connect to specific technology** and **requires a different set of input parameters.**

The connections types can be divied into multiple categories:
Prefix|Category
------|--------
db|Database connectivity
fwk|Framework capabilities
host|Operating system connectivity
http|Http-based connectivity
repo|Repository connectivity
socket|Socket connectivity

These categories contain pre-defined connection types. Consult the detailed overview of the connection types here.

### *Configure Connections*

Just like the environment configurations, it is possible to configure or update the connections by using the connectivity template.
  *	Define the connections in the Connections sheet
  *	For each connection a configuration line is added per environment
      * Define a name and description
      *	Select the correct connection type
      *	Link the configuration line to the appropriate environment name
      *	Complete the parameters and values corresponding to the connection type

When finished, the names of the connections can be shared with the automation engineers. The connectivity details can be managed centrally without requiring to disclose this information.

**Important:** 
  * The name of the connection needs to be unique as it is used as identifier in the configuration of the scripts
  *	Passwords need to be encrypted via the bin/iesi-encrypt.sh (or iesi-encrypt.cmd on Windows) command. 

## Components
Components are created in order to provide the automation engineers with high reusability and parameterization. The component type is used as a **switch for the connectivity options** and is **stored in a component-library.** 

A component is defined with its technical details (e.g. name/path) and the type (e.g. http/other), and is given a logical name in order to be called upon during script execution. 

For example, a http-call can therefore be configured once, be stored in the components library and used in the creation of other scripts where same logic needs to be used. 

### *Configure Components*
It is possible to configure the components or update them using a component template.
  *	Define the (logical) name, description and version for the component 
  *	Define the technical details (e.g. name/path)
  *	Define the type
  *	Apply parameterization if possible

**Important:** The name of the component needs to be unique as it is used as a identifier in the configuration of the scripts

