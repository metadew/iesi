# Configure your script - Using Subroutines
## What are subroutines?
Subroutines is a capability to **define reusable snippets of code** that can be used as **part of action parameter values**. In that way, a similar piece of configuration needs to be defined only once and can be reused many times. It can be compared to a **spreadsheet function**. As a user, you need to know the function name and what it does, not how it works in the background.

There are two categories of subroutines: 
* **built-in subroutines** (provided by the framework
* **user-defined subroutines** (defined by the automation engineer)


### Built-in subroutines
Built-in subroutines cover different types related to **lookups**, **data generation**, **variable retrieval** and much more. The outcome of the subroutine will be **substituted in the parameter field.**

**How are subroutines defined?** \
The syntax of a subroutine contains out of the following elements: `{{<instruction><subroutine><args>}}`

* `<instruction>`: refers to the functionality ~ i.e. look-up, data generation, ...
* `<subroutine>`: refers to the name of the function ~ i.e. person.firstname, connection, time.travel, ...
* `<args>`: refers to the parameters that need to be used as input for the subroutine function

Subroutines can also be **nested**, containing subroutines in subroutines (e.g. for date formatting)

**Instruction items** (`<instruction>`)
|Syntax|Function|Description|
|------|--------|-----------|
|=|Look-up|Lookup relevant information|
|* |Generate|Generate synthetic data on the fly|
|$|Variable|Get the variable of a specific (framework) variable|


**Subroutine items** (`<subroutine><args>`)
|Function|Instruction syntax|Subroutine|Description       |Subroutine syntax|Example|Output|
|--------|------------------|----------|-----------|-----------------|-------|------|
|**Look-up**|
|        |=                 |connection|Lookup connection parameter value|{{=connection(X,Y)}}|{{=connection(petsTutorial,port)}}|8800|
|        |=                 |dataset|Lookup a value from a dataset|{{=dataset(X,Y)}}|{{=dataset(datasetInput,firstname)}}|Jane|
|        |=                 |environment|Lookup an environment parameter value |{{=environment(X,Y)}}|{{=environment(variableTest,envName)}}|prd|
|        |=                 |file|Lookup file content|{{=file(X)}}|{{=file(test)}}|[test]|
|        |=                 |coalesce|Resolve variable value if provide, otherwise, take default value|{{=coalesce(X,Y)}}|{{=coalesce(#city#,BRUSSEL)}} - With city= [empty]\{{=coalesce(#city#,BRUSSEL)}} - With city=NAMEN|BRUSSEL NAMEN|
|**Generate**|
|        |*                 |belgium.nationalRegisterNumber|Get a Belgian national register number conform to the standard format|{{*belgium.nationalRegisterNumber(X,Y)}} X= Date of birth - Y= Gender (1 = Male & 2= Female)|{{*belgium.nationalRegisterNumber(30111995,1)}}|95113058307|
|        |*                 |date.between|Get a random date between a defined range in format ddMMyyyy|{{*date.between(ddMMyyyy,ddMMyyyyy)}}|{{*date.between(30111995,01012000)}}|16101996|
|        |*                 |date.format|Format a date|{{*date.format(X,Y)}} - X= original format - Y= new format|{{*date.format(30111995, yyyyMMdd)}} {{*date.format({{*date.today()}},yyyyMMdd)}} {{*date.format({{*date.today()}},dd/MM/yyyy)}}|19951130 20210402 02/04/2021|
|        |*                 |date.today|Generates today’s date|{{*date.today()}} - format: ddMMyyyy|{{*date.today()}}|02042021|
|        |*                 |date.travel|Generates a date in the future  with nr of days, months, years|{{*date.travel(X,Y,Z)}} - X = date which you would like to travel from - Y = travel in days, months, years - Z = amount of days, months, years|{{*date.travel({{*date.today()}},”day”,1}} {{*date.travel({{*date.today()}},”month”,1}} {{*date.travel({{*date.today()}},”year”,1}}|03042021 02052021 02042022|
|        |*                 |date.travel|Generates a date in the past with nr of days, months, years|{{*date.travel(X,Y,-Z)}} - X = date which you would like to travel from - Y = travel in days, months, years - Z = amount of days, months, years|{{*date.travel({{*date.today()}},”day”,-1}} {{*date.travel({{*date.today()}},”month”,-1}} {{*date.travel({{*date.today()}},”year”,-1}}|01042021 01032021 01032020|
|        |*                 |date.travel|Generates a date in the future/past with only weekdays (weekends excl.)|{{*date.travel(A,B,C,D)}} - A = date which you would to travel from - B= travel in days - C= amount of days - D= indicate only weekdays|{{*date.travel({{*date.today()}},”day”,2,W)}}|Only weekday|
|        |*                 |time.format|Format a timestamp|{{*time.format(X,Y)}} - X= original format - Y= new format|{{*time.format({{*time.now()}},yyyyMMddHHmmss)}}{{*time.format({{*time.now()}},yyyyMMddHHmm)}} {{*time.format({{*time.now()}},yyyy-MM-dd HH:mm:ss.SSS)}}|20210402093846 202104020938 2021-04-02 09:38:46.106|
|        |*                 |time.now|Generates the current timestamp|{{*time.now()}} - Format: yyyy-MM-dd HH:mm:ss.SSS|{{*time.now()}}|2021-04-02 09:38:46.106|
|        |*                 |time.travel|Generates a timestamp in the future with nr of hours, minutes, seconds|{{*time.travel(X,Y,Z)}} - X = time which you would like to travel from - Y = travel in hours, minutes, seconds - Z = amount of hours, minutes, seconds|{{*time.travel ({{*time.now()}},”hour”,5)}} {{*time.travel ({{*time.now()}},”minute”,5)}} {{*time.travel ({{*time.now()}},”second”,5)}}|2021-04-02 14:38:46.106 2021-04-02 09:43:46.106 2021-04-02 09:38:51.106|
|        |*                 |time.travel|Generates a timestamp in the past with nr of hours, minutes, seconds|{{*time.travel(X,Y,-Z)}} - X = time which you would like to travel from - Y = travel in hours, minutes, seconds - Z = amount of hours, minutes, seconds|{{*time.travel ({{*time.now()}},”hour”,-5)}} {{*time.travel ({{*time.now()}},”minute”,-5)}} {{*time.travel ({{*time.now()}},”second”,-5)}}|2021-04-02 04:38:46.106 2021-04-02 09:33:46.106 2021-04-02 09:38:41.106|
|        |*                 |person.email|Generates a random email address|{{*person.email()}}|{{*person.email()}}|test@tester.com|
|        |*                 |person.firstname|Generates a random firstname|{{*person.firstname()}}|{{*person.firstname()}}|Jane|
|        |*                 |person.lastname|Generates a random lastname|{{*person.lastname()}}|{{*person.lastname()}}|Doe|
|        |*                 |person.phonenumber|Generates a random phonenumber|{{*person.phonenumber()}}|{{*person.phonenumber()}}|00712345684|
|**Variable**|
|        |$                 |<variablename>|Get the value of a specific (framework) variable|{{$[variablename]}}|{{$run.id}}|0214587956214dds7adsdf4554445|
|        |$                 |<variablename>|Get the value of a specific (framework) variable|{{$[variablename]}}|{{$process.id}}|445|


### User-defined subroutines
TBD



### Testing of subroutines
Subroutines can be easily used in datasets and scripts. Want to test/try it first? Find an example below:

```yaml
---
type: "script"
data:
  name: "TestScript"
  description: "Script to test subroutine functions"
  parameters: []
  actions:
  - number: 1
    type: "fwk.setParametervalue"
    name: "test"
    description: "test the subroutine function"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "name"
      value : "test"
    - name: "value"
      value : "{{*date.today()}}"
```
