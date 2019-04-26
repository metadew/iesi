{% include navigation.html %}

# Define the system landscape

Before running the automation framework it is required to define the connectivity end points. 
On which systems will the actions be performed? How can the automation framework connect to these systems? 
These configuration settings are defined upfront (and whenever they change) so that automation engineers do not need to worry about this. 
Instead it will be possible to design automation scripts using the logical names that are defined. 
The details will be retrieved during the execution.

## Environments

In an enterprise environment there is a clear distinction between development, test and production environments. 
In order to make the distinction between the different connectivity end points linked to any environment, each connection needs to be linked to one environment. 
When designing the automation script, the environment name is not added to the configuration. 
Rather, it is provided when starting the execution as one of the input parameters. 
As such, the same automation script can be executed on any environment.

Similarly to the connection configuration, logical names for the definition of the environments are used. 
This means that your context can easily be mapped regardless of the number or scope of instances. 

|Environment|Description|
|---|---|---|
|dev|Development environment|
|test|Test enviroment|
|...|...|
|prod|Production environment|

### Configure environments

It is possible to configure the necessary environments or update them using the connectivity template. 
* Define the name and description for the environments in the `Environments` sheet
* Define parameters for the different environments in the `EnvironmentParameters` sheet
  * Parameters are loaded at the start of any execution
  * Typically used for environment dependent configurations such as hostnames, environment configuration files, etc.

**Important**
* The name of the environment needs to be unique as it is used a identifier in the configuration of the connections

## Connections

Next, the connections can be defined. 
Each system that the automation framework will connect to is configured for any relevant enviroment. 
Depending on the type of system, the framework has embedded the notion of connection types. 
When defining the connection settings, the automation engineer can make use of these to quickly establish connectivity. 

### Connection types

Connection types are the reusable configuration blocks. 
Each type allows the framework to connect to specific technology and requires a different set of input parameters. 

<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Connection Type</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.ConnectionTypes %}
<tr>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

More information can be found [here](/{{site.repository}}/pages/define/connectiontypes.html).

### Configure connections

It is possible to configure the necessary connections or update them using the connectivity template.
* Define the connections in the `Connections` sheet
* For each connection a configuration line is added per enviroment
  * Define a name and description
  * Select the correct connection type
  * Link the configuration line to the appropriate environment name
  * Complete the parameters and values corresponding to the connection type

When done, the names of the connections can be shared with the automation engineers. 
The connectivity details can be managed centrally without requiring to disclose this information.

**Important**
* The name of the connection needs to be unique as it is used a identifier in the configuration of the scripts
* Passwords need to be encrypted via the `bin/iesi-encrypt.sh` (or `iesi-encrypt.cmd` on Windows) command. [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/operate/operate.html)

### Impersonations

Sometimes it can be useful to replace one or more configured connections by other ones. 
The automation framework refers to this concept as *impersonations*. 
It can be used to make script configurations even more generic and bind connection information only when the script is executed. 
Some examples where this has been applied include:
* Data center indepent configurations for disaster recovery purposes
* Automating application life cycle migration and testing where both versions are running in parallel

In order to make use of impersonations the appropriate parameters need to be provided when starting the script execution:
* Provide the required impersonations using the `impersonate` option
* Define an impersonation profile configuration and provide this as input using the `impersonation` option 

More information can be found [here](/{{site.repository}}/pages/operate/operate.html).

It is possible to configure the necessary impersonations or update them using the connectivity template.
* Define the connections in the `Impersonations` sheet
* For each impersonation a configuration line is added per item
  * Provide a mapping between configured connection name and the appropriate impersonation connection name
  * Optionally a description can be added for information purposes

When done, the names of the impersonations can be shared with the automation engineers. 
Similarly to other connectivity details these can be managed centrally without requiring to disclose the information.

**Important**

* The name of the impersonation needs to be unique as it is used a identifier when using during execution
* To make use of impersonations, there are two important conditions:
  * The connection types are the same for both connections
  * Both connections are configured for the environment where the automation script is executed

## Components

We are working providing more details on this.