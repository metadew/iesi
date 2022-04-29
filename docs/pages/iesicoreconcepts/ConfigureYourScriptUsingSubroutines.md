{% include navigation.html %}
# Configure your script - Using Subroutines/Instructions
## What are subroutines/instructions?
Subroutines is a capability to **define reusable snippets of code** that can be used as **part of action parameter values**. In that way, a similar piece of configuration needs to be defined only once and can be reused many times. It can be compared to a **spreadsheet function**. As a user, you need to know the function name and what it does, not how it works in the background.

There are two categories of subroutines: 
* **Built-in subroutines** (provided by the framework)
* **User-defined subroutines** (defined by the automation engineer)


### Built-in subroutines
Built-in subroutines cover different types related to **lookups**, **data generation**, **variable retrieval** and much more. The outcome of the subroutine will be **substituted in the parameter field.**

**How are subroutines defined?** \
The syntax of a subroutine contains out of the following elements:

* `<instruction>`: refers to the functionality - i.e. look-up, generate or variable
* `<subroutine>`: refers to the name of the function - i.e. person.firstname, connection, time.travel, ...
* `<args>`: refers to the parameters that need to be used as input for the subroutine function

Subroutines can also be **nested**, containing subroutines in subroutines (e.g. for date formatting)

**Instruction Function** (`<instruction>`)

|Syntax|Function|Description|
|------|--------|-----------|
|=|Look-up|Lookup relevant information|
|* |Generate|Generate synthetic data on the fly|
|$|Variable|Get the variable of a specific (framework) variable|

**Subroutine Types** (`<subroutine><args>`)

|Instruction Type|Subroutine Type|Description|
|----------------|---------------|-----------|
|[**Look-up**](/{{site.repository}}/pages/iesicoreconcepts/Instructions/lookupinstruction.html)|                 |All instructions related to data look-up|
|**Generate**|                |All instructions related to data generation|
|            |[Generic instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generategenericinstruction.html)|Generating and formatting of person values|
|            |[Date instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatedateinstruction.html)|Generating and formatting of date values|
|            |[Time instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatetimeinstruction.html)|Generating and formatting of time values|
|            |[Number instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatenumberinstruction.html)|Generating and formatting of number values|
|            |[List instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatelistinstructions.html)|Generating and formatting of list values|
|            |[Math instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatemathinstruction.html)|Generating and formatting of math formulas|
|            |[Text instructions](/{{site.repository}}/pages/iesicoreconcepts/Instructions/generatetextinstruction.html)|Generating and formatting of string - xml - json responses|
|[**Variable**](/{{site.repository}}/pages/iesicoreconcepts/Instructions/variableinstruction.html)|                 |All instructions related to fetching of framework variables


### Testing of subroutines
Subroutines can be easily used in datasets and scripts. Want to test/try it first? Find an example below:

```yaml
---
type: "script"
data:
  name: "Example 1"
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
```yaml
---
type: "script"
data:
  name: "Example 2"
  description: "Script to test subroutine functions"
  parameters: []
  actions:
  - number: 1
    type: "fwk.outputMessage"
    name: "test"
    description: "test the subroutine function"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "message"
      value : "{{*date.today()}}"
    - name: "onScreen"
      value : "Y"
```
