{% include navigation.html %}

# Look-up
## Overview
This page contains all information on the data look-up instruction(s).

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|=|connection|Lookup connection parameter value|
|=|dataset|Lookup dataset parameter values|
|=|environment|Lookup an environment parameter value|
|=|file|Lookup file content|
|=|coalesce|Lookup variable value when provided, otherwise, take default value|


### connection
```
Description: Lookup connection parameter value
Subroutine Syntax: {{=connection(X,Y)}}
  <X>: connection name
  <Y>: connection parameter

Example:
{{=connection(petsTutorial,port)}}

Example Output:
8800
```
### dataset
```
Description: Lookup dataset parameter values
Subroutine Syntax: {{=dataset(X,Y)}}
  <X>: dataset name
  <Y>: dataset parameter

Example:
{{=dataset(datasetInput,firstname)}}
{{=dataset(datasetOutput,firstname)}}

Example Output:
Jane
```
### environment
```
Description: Lookup an environment parameter value
Subroutine Syntax: {{=environment(X,Y)}}
  <X>: environment name
  <Y>: environment parameter

Example:
{{=environment(variableTest,envName)}}

Example Output:
Test
```
### file
```
Description: Lookup file content
Subroutine Syntax: {{=file(X)}}
  <X>: file name

Example:
{{=file(test)}}

Example Output:
[test]
```
### coalesce
```
Description: Lookup variable value when provided, otherwise, take default value
Subroutine Syntax: {{=coalesce(X,Y)}}
  <X>: variable name
  <Y>: default value

Example 1:
{{=coalesce(#city#,BRUSSEL)}}
With parameter value for <city>=[empty]

Example 1 Output:
BRUSSEL

Example 2:
{{=coalesce(#city#,BRUSSEL)}}
With parameter value for <city>=NAMEN

Example 2 Output:
NAMEN
```
