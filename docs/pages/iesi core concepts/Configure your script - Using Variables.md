# Configure your script - Using Variables
## What is a variable?
Variables allow automation scripts to be designed in a generic manner where **execution depends on user input or execution context**. In this way, it is possible to create reusable configurations that can be executed many times without requiring additional configuration. This is a great way of multiplying the number of automation execution fast and easy.

* **User input**: A user can define one or multiple parameters when starting *script execution*. These parameters can be provided via a specific file (e.g. dataset) or through the UI's execution window.
* **Execution context**: Variables can be stored during the execution of an *action* and eb used afterwards. This can be done by using the related action types.

## How is a variable defined?
Variables are always defined as **key-value pairs** and  defined with the `#` symbol before and after the variable name: `#variable1#`.

**Characteristics of a variable:**
  * A variable is **resolved during execution**
  * A variable **name** needs to be **unique** for the highest level of execution (i.e. script of parent script)
  * If same variable names are loaded in the same execution context, the previous value is overwritten. 
  * The variables are no longer available at the end of the execution

**During the design of the script, the use of one or more variables can be included:**
  * `##`-symbol: to define a variable (e.g. `#variable1#`)
  * `[]`-symbol: to refer to another parameter in the same action (e.g. `[parameterName]`)
  * `[##]`-symbol: to refer to an interation parameter inside an action's iteration (e.g. `[#iterationVariableName#]`)



## Examples
### Apply variables in a script

**Example with fixed values**
```yaml
---
type: "script"
data:
  name: "OriginalScript"
  description: "API call using fixed values"
  parameters: []
  actions:
  - number: 1
    type: "http.executeRequest"
    name: "sepaTransfer"
    description: "set body for the transaction"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "request"
      value : "test"
    - name: "body"
      value : "{"origin_account": {"iban": "BE32123456789101"},"remote_account": {"iban": "BE32987654321236"}}"
    - name: "...."
      value : "...."
```
**Example with a variable**
```yaml
---
type: "script"
data:
  name: "OriginalScript"
  description: "API call using fixed values"
  parameters: []
  actions:
  - number: 1
    type: "http.executeRequest"
    name: "sepaTransfer"
    description: "set body for the transaction"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "request"
      value : "test"
    - name: "body"
      value : "{"origin_account": {"iban": "BE32123456789101"},"remote_account": {"iban": "#accountNumber#"}}"
    - name: "...."
      value : "...."
```

When now executing the script, we provide the value of our variable at runtime through the **'Input Parameters'** functionality in the UI. Once provided, **the parameters will be resolved during execution.**

Thus, we add the following input parameters in the execution window:
  * Name: **accountNumber** - Value: **BE32987654321236**

### Apply variables in a component


### Apply variables in a dataset
### Apply variables during runtime
