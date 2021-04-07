# Configure your script - Using Subroutines
## What are subroutines?
Subroutines is a capability to **define reusable snippets of code** that can be used as **part of action parameter values**. In that way, a similar piece of configuration needs to be defined only once and can be reused many times. It can be compared to a **spreadsheet function**. As a user, you need to know the function name and what it does, not how it works in the background.

There are two categories of subroutines: 
* built-in subroutines (provided by the framework
* user-defined subroutines (defined by the automation engineer)


### Built-in subroutines
Built-in subroutines cover different types related to **lookups**, **data generation**, **variable retrieval** and much more. The outcome of the subroutine will be **substituted in the parameter field.**

**How are subroutines defined?** \
The syntax of a subroutine contains out of the following elements: `<instruction><subroutine><args>`

* `<instruction>`: refers to the functionality ~ i.e. look-up, data generation, ...
* `<subroutine>`: refers to the name of the function ~ i.e. person.firstname, connection, time.travel, ...
* `<args>`: refers to the parameters that need to be used as input for the subroutine function


**Instruction items** (`<instruction>`)
|Syntax|Function|Description|
|------|--------|-----------|
|=|Look-up|Lookup relevant information|
|* |Generate|Generate synthetic data on the fly|
|$|Variable|Get the variable of a specific (framework) variable|


**Subroutine items** (`<subroutine><args>`)
|Function|Instruction syntax|Subroutine|Description|Subroutine syntax|Example|Output|
|--------|------------------|----------|-----------|-----------------|-------|------|


## Subroutines items


## Give it a try?
