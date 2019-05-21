{% include navigation.html %}

# Design automation scripts

The automation framework focuses on configuring scripts once and executing them many times. 
To achieve this in the most effective and efficient manner, automation engineers can make use of a variety of reusable tools and constructs. 
It is possible to automate a single use case in many different ways. 
It is the engineer's level of skill, creativity and design expertise that will result extremely powerful and effective automation solutions.

## The automation script

Central to any automation is the script. 
In a script, it is possible to define a set of actions that needs to be executed in a specific sequence. 
An action is the lowest level of execution that exists. 
It takes a set of input parameters and performs a single (set of) operation(s) generating a technical outcome. 
Depending on the type of operation, the framework has embedded the notion of action types. 
During the design of the script, the automation engineer can make use of these types to quickly create a script. 
The framework will help limiting the effort by focusing on required parameters only.

Examples of actions include:
* execute a file transfer
* execute a command from the commandline
* execute a sql query on a database
* etc.

### Action types

Action types are the reusable building blocks of automation scripts. 
Each type performs a specific operations and requires a different set of input parameters. 

Look at these types as building blocks. On their own, the perform a specific operation. 
But when put in different sequences, they will perform any set of scripted actions. 
They are the bricks of our automation framework. 

![building-blocks](/{{site.repository}}/images/introduction/building-blocks.png)

<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Action Type</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for actiontype in site.data.ActionTypes %}
<tr>
<td markdown="span">[{{ actiontype.data.name }}](/{{site.repository}}/pages/design/actiontypes/{{ actiontype.data.name }}.html)</td>
<td markdown="span">{{ actiontype.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

An overview of the different action types can be found [here](/{{site.repository}}/pages/design/actiontypes.html).

### Configure script

It is possible to configure or update the script using the script template. 
The template contains a `script` sheet that can be used for designing the automation sequence. 
As an automation engineer you are able to copy/paste this sheet as required. 
In this way, your automation scripts can be organized across and inside several templates. 

> Make use of Excel functionality to accelerate authoring automation scripts

* Define the name and description for script in the `script` sheet
* (optional but recommended) Add a version number and description
  * The version number needs to be an integer starting at 1 and incrementing when needed
  * When fixing issues in a script you might want to keep the version unchanged, 
  however when updating the script to a new component version incrementing the version number is appropriate
* Add a line item per action to execute
  * Add an action number
  * Select the correct action type
  * Define a name and description for the action
  * (optional) Specify a component name related to the action
    * This allows to make the relation with the component library
	* When the component is specified, its attributes are be available to be used as parameters
  * Specify an iteration name to use if needed. This will result in making use of an iteration definition that is defined before and iterate over this action.
  * Specify a condition to use if needed. A condition is used in order to skip this action if the outcome is evaluated as false. The action is only executed if the condition is evaluated as true.
  * Specify the number of times to retry a specific action if an error occurs. Stop on error will override the retry definition.
  * Set the expected error flag `Y/N`
    * This allows to define the behaviour of the framework for negative testing
	* If the flag is set to `Y` then an error in the execution is considered as a successful execution
	* More information can be found [here](/{{site.repository}}/pages/design/expectederrors.html)
  * Set the stop on error flag `Y/N`
    * This allows to perform multiple tests and gather the complete feedback in a single run; 
	but also to avoid corruption of an environment if a mandatory prerequisite action has failed
    * If the flag is set to `Y` then the script will stop execution if the action execution ends in error
  * Complete the parameters and values corresponding to the action type
    * In most action types, one of these parameters will be a connection name. 
	Here, the value from the connection configuration needs to be used [![info](/{{site.repository}}/images/icons/question-dot.png)](/{{site.repository}}/pages/define/define.html)

**Important**

* The name of the script needs to be unique as it is used a identifier in the framework
* The name of the action inside the script needs to be unique as it is used a identifier in the script

## Using variables

Variables allow automation scripts to be designed in a generic manner where execution depends on user input or execution context. 
In this way, it is possible to create reusable configurations that can be executed many times without requiring additional configuration. 
This is a great way of multiplying the number of automation execution fast and easy.

During the design of the script configuration the use of one or more variables can be included.

* To define a variable, it is sufficient to identify it with the `#` symbol before and after its name: `#variable#`.
* To make reference to another parameter in the same action, this can be identified with the `[]` symbols: [parameterName]`
* To make reference to an iteration parameter inside an action's iteration, this can be identified with the `[##]` symbols: `[#iterationVariableName#]`

More information on using variables can be found [here](/{{site.repository}}/pages/understand/understand.html).

## Using subroutines

Subroutines are a capability to define reusable snippets of code that can be used as part of action parameter values. 
In that way, a similar piece of configuration needs to be defined only once and can be reused many times. 
It can be compared to a spreadsheet function. 
As a user, you need to know the function name and what it does, not how it works in the background.

There are two categories of subroutines: built-in subroutines or subroutines defined by the automation engineer. 
Built-in subroutines cover different types covering lookups, data generation, variable retrieval and much more. 
The outcome of the subroutine will be substitured in the parameter field. 
In this way we make access to common functions easier and more convenient.

In order to use subroutines, the syntax `{{<instruction><subroutine>(<args>}}` can be applied.
  * The `<instruction>` refers to its functionality: lookup, data generation, etc.
  * The '<subroutine>` is the name of the function: connection, environment, etc.
  * The `<args> define the parameters that need to be used as input for the subroutine

At the same time, all variables that are defined inside the subroutine's argument are also replaced on runtime. 
In this way, the subroutine can be used in a truly reusable manner.

More information on defining and using subroutines can be foud [here](/{{site.repository}}/pages/design/subroutines.html).

