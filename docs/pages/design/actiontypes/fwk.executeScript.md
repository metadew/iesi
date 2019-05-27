{% include navigation.html %}

# fwk.executeScript

This action executes another script inside the current script's execution. 
It allows to reuse other scripts to create a modular, scalable and maintainable automation design.
* Common actions can be designed once and reused many times. When an update is needed, this is only required once.
* Actions that are sensitive for updates can be versioned separately from the main script
* New automation designs can be added in steps

## Use cases

* Create reusable scripts containing common actions; for instance:
  * Setting the runtime environment
  * Executing a processing flow
  * Preparing an environment for testing (cleaning, provisioning, etc.)
  * Etc.
* Separate actions that are sensitive for updates and create separate versions

## Parameters

### 1: script

`script: "script name"`
* The script name for which needs to be executed

### 2: version

`version: "1"`
* The version for the script that needs to be selected
* if no value is provided, the latest version will be selected

### 3: paramlist

`paramlist: "parameter list"`
* Define the list of parameters to use: `-paramlist key=value`
* Multiple values are allowed separated by commas: `-paramlist key1=value1,key2=value2`
* an option for a single parameter does not exist, it is considered as one item in this paramlist

### 4: paramfile

`paramfile: "parameter file path"`
* Define a parameter file to use: `-paramfile /path/file.ext`
* Multiple values are allowed separated by commas: `-paramfile /path/file1.ext,/path/file2.ext`


## Examples

```yaml
  - number: 1
    type: "fwk.executeScript"
    name: "example1"
    description: "Exectue version 1 of script"
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

```yaml
  - number: 2
    type: "fwk.executeScript"
    name: "example1"
    description: "Execute latest version of script"
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
*Note*: the parameter version can also been removed from the yaml definition since it is empty.
