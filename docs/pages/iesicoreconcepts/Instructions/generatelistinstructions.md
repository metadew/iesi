{% include navigation.html %}

# Generate - List Instructions
## Overview
This page contains all information on generating and formatting of list values

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |list.size|Checks the element count in a list|


### list.size
```
Description: Generates a random number in defined range
Subroutine Syntax: {% raw %} {{*list.size(X)}} {% endraw %}
  <X>: listname or listreference

Example 1:
{% raw %}{{*list.size({{=dataset(OutputDataset,list)}})}}{% endraw %}

Example 1 Output:
5

Example 2:
{% raw %}{{*list.size(listReference)}}{% endraw %}

Example 2 Output:
5
```
