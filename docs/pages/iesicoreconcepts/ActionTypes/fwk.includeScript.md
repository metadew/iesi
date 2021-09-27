{% include navigation.html %}
## fwk.includeScript
## Purpose
This action injects the actions of a specific script into the current script. 
It allows to reuse other scripts to create a modular, scalable and maintainable automation design.
* Common actions can be designed once and reused many times. When an update is needed, this is only required once.
* Actions that are sensitive for updates can be versioned separately from the main script
* New automation designs can be added in steps

*Use Cases*
* Create reusable scripts containing common actions; for instance:
  * Setting the runtime environment
  * Executing a processing flow
  * Preparing an environment for testing (cleaning, provisioning, etc.)
  * Etc.
* Separate actions that are sensitive for updates and create separate versions

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|script|Script name to execute|string|Y|N|
|version|Version of the script to run|string|N|N|


## Example 1
```yaml
  - number: 1
    type: "fwk.includeScript"
    name: "example1"
    description: "Include version 1 of script"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "script"
      value : "helloworldversion"
    - name: "version"
      value : "1"
```
## Example 2
```yaml
  - number: 2
    type: "fwk.includeScript"
    name: "example1"
    description: "Include latest version of script"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "script"
      value : "helloworldversion"
    - name: "version"
      value : ""
```
*Note*: the parameter version can also been removed from the yaml definition since it is empty. When no version is provided, the latest version will be selected.
