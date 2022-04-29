{% include navigation.html %}

# Variable
## Overview
This page contains all information related to fetching of framework variables

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|$ ||Get the value of a specific (framework) variable|


### framework variables
```
Description: Fetch the framework variable parameter
Subroutine Syntax: {% raw %}{{$[variablename]}}{% endraw %}

Example 1:
{% raw %}{{$run.id}}{% endraw %}

Example 1 Output:
0214587956214dds7adsdf4554445

Example 2:
{% raw %}{{$process.id}}{% endraw %}

Example 1 Output:
445
```
