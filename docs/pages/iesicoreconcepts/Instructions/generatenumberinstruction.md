{% include navigation.html %}

# Generate - Number Instructions
## Overview
This page contains all information on generating and formatting of number values

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |number.between|Generates a random number in defined range|
|* |number.format|Formats a number definition|


### number.between
```
Description: Generates a random number in defined range
Subroutine Syntax: {{*number.between(X,Y,Z)}}
  <X>: start range
  <Y>: end range
  <Z>: decimals (optional)

Example 1:
{{*number.between(1,10)}}

Example 1 Output:
4,14785689487523

Example 2:
{{*number.between(1,10,0)}}

Example 2 Output:
5
```
### number.format
```
Description: Formats a number definition
Subroutine Syntax: {{*number.format(X,Y)}}
  <X>: number
  <Y>: format

Example:
{{*number.format(1,"0.00")}}

Example Output:
1.00
```
