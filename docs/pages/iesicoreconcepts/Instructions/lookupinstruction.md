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
Subroutine Syntax: {% raw %}{{=connection(X,Y)}}{% endraw %}
  <X>: connection name
  <Y>: connection parameter

Example:
{% raw %}{{=connection(petsTutorial,port)}}{% endraw %}

Example Output:
8800
```
### dataset
```
Description: Lookup dataset parameter values
Subroutine Syntax: {% raw %}{{=dataset(X,Y)}}{% endraw %}
  <X>: dataset name
  <Y>: dataset parameter

Example:
{% raw %}{{=dataset(datasetInput,firstname)}}{% endraw %}
{% raw %}{{=dataset(datasetOutput,firstname)}}{% endraw %}

Example Output:
Jane
```
### environment
```
Description: Lookup an environment parameter value
Subroutine Syntax: {% raw %}{{=environment(X,Y)}}{% endraw %}
  <X>: environment name
  <Y>: environment parameter

Example:
{% raw %}{{=environment(variableTest,envName)}}{% endraw %}

Example Output:
Test
```
### file
```
Description: Lookup file content
Subroutine Syntax: {% raw %}{{=file(X)}}{% endraw %}
  <X>: file name

Example:
{% raw %}{{=file(test)}}{% endraw %}

Example Output:
[test]
```
### coalesce
```
Description: Lookup variable value when provided, otherwise, take default value
Subroutine Syntax: {% raw %}{{=coalesce(X,Y)}}{% endraw %}
  <X>: variable name
  <Y>: default value

Example 1:
{% raw %}{{=coalesce(#city#,BRUSSEL)}}{% endraw %}
With parameter value for <city>=[empty]

Example 1 Output:
BRUSSEL

Example 2:
{% raw %}{{=coalesce(#city#,BRUSSEL)}}{% endraw %}
With parameter value for <city>=NAMEN

Example 2 Output:
NAMEN
```
