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
Subroutine Syntax: {{*list.size(X)}}
  <X>: listname or listreference

Example 1:
{{*list.size({{=dataset(OutputDataset,list)}})}}

Example 1 Output:
5

Example 2:
{{*list.size(listReference)}}

Example 2 Output:
5
```
