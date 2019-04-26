{% include navigation.html %}

# Subroutines

Subroutines are a capability to define reusable snippets of code that can be used as part of action parameter values. 
There are two categories of subroutines:
* Built-in subroutines that are provided by the framework
* User-defined subroutines that can be customized depending on the automation need

## Built-in subroutines

Using built-in subroutines is easy, just call it using the appropriate input parameters. 
As any function or procedure, the parameters that need to be provided depend on the functionality which is supported. 
It is important to pay attention to the correct usage of the syntax. 

Subroutines are always identified using the following syntax : `{{<instruction><subroutine>(<args>}}`
* The `<instruction>` defines the type of subroutine: lookup, generation, execution, etc.
* The `<subroutine>` part identifies the name of the subroutine to execute 
Note that synonyms of a same subroutine are also supported
* The (optional) `<args>` section allows to specify any input parameter required for the subroutine

### Subroutine types

|Instruction|Title|Description|
|:---:|---|---|
|=|Lookup|Lookup relevant information|
|*|Generate|Generate synthetic data on the fly|
|$|[Variable](/{{site.repository}}/pages/design/subroutines/variableinstructions.html)|Get the value of a specific (framework) variable|

### Subroutine items

|Instruction|Subroutine|Synonyms|Description|Syntax|Output|
|:---:|---|---|---|---|
|=|connection|conn|Lookup connection parameter value|=connection(<connectionName>,<connectionParameterName>)||
|=|dataset|ds|Lookup value from a dataset|=dataset(<datasetName>,<datasetItem>)||
|=|environment|env|Lookup environment parameter value|=environment(<environmentName>,<environmentParameterName>)||
|=|file|f|Lookup file content|=file(<filePath>)||
|*|belgium.nationalregisternumber||Get a random Belgian national registry number|=belgium.nationalregisternumber()||
|*|date.between||Get a random date between two date|=date.between(<startDate:ddMMyyyy>,<endDate:ddMMyyyy>)|date:ddMMyyyy|
|*|date.format||Format a date|=date.format(<date:ddMMyyyy>,<format>)|date:format|
|*|date.today||Get today's date|=date.today()|date:ddMMyyyy|
|*|date.travel||Travel a date with a number of days, months or years|=date.travel(<date:ddMMyyyy>,<granularity:year\|month\|day>,<interval>)|date:ddMMyyyy|
|*|person.email||Get a random email address|=person.email()||
|*|person.firstname||Get a random first name|=person.firstname()||
|*|person.lastname||Get a random last name|=person.lastname()||
|*|person.phonenumber||Get a random phone number|=person.phonenumber()||
|*|time.format||Format a timestamp|=time.format(<timestamp:yyyy-MM-dd HH:mm:ss.SSS>,<format>)|timestamp:format|
|*|time.now||Get the current timestamp|=time.now()|timestamp:yyyy-MM-dd HH:mm:ss.SSS|
|*|time.travel||Travel a timestamp with a number of hours, minutes or seconds|=time.travel(<timestamp:yyyy-MM-dd HH:mm:ss.SSS>,<granularity:hour\|minute\|second>,<interval>)|timestamp:yyyy-MM-dd HH:mm:ss.SSS|

## User-defined subroutines

Using user-defined subroutines consists of two parts:
* Defining the subroutine including the configuration it includes
* Using the subrouting as part of an action parameter value

### Subroutine types

Subroutine types are the reusable configuration blocks of action type parameter values. 
Each type allows the framework to use them for specific action type parameters and requires a different set of input parameters. 

<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Subroutine Type</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for subroutinetype in site.data.SubroutineTypes %}
<tr>
<td markdown="span">{{ subroutinetype.data.name }}</td>
<td markdown="span">{{ subroutinetype.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

More information can be found [here](/{{site.repository}}/pages/design/subroutinetypes.html).

### Defining subroutines

It is possible to configure or update subroutines using the subroutine template.
* Define the subroutines in the `Subroutines` sheet
* For each subroutine a configuration line is added
  * Define a name and description
  * Select the correct subroutine type
  * Complete the parameters and values corresponding to the subroutine type

When done, the subroutines can be used in the scripts via their name.

**Important**
* The name of the subroutine needs to be unique as it is used a identifier in the configuration of the scripts

### Using subroutines

In order to use subroutines in action type parameter values, the following syntax applies:

```
=srt([SUBROUTINE])
where SUBROUTINE is the name of the subroutine
```

The subroutine will be substituted in the parameter field. 
At the same time, all parameters that are defined inside the subroutine are also replaced on runtime. 
In this way, the subroutine can be used in a truly reusable manner.

**Illustration**
* Define a subroutine named `executeJob` that has a parameter for the job variable name `#job#` (for instance: `./executeJob.sh –p #job#`)
* An automation engineer only needs to call the subroutine `=srt(executeJob)` to run any job
* The parameter `#job#` will be replaced at runtime
* As such, the logic to run the job is hidden to the script and not needed anymore

**Note**
* Binding parameters inside the subroutine call is not yet supported