{% include navigation.html %}

# Generate - Math Instructions
## Overview
This page contains all information on generating and formatting of math formulas

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |math.add|Formula to perform an addition|
|* |math.divide|Formula to perform a division|
|* |math.power|Formula to perform an exponentiation|
|* |math.multiply|Formula to perform a mutiplication|
|* |math.substract|Formula to perform a substraction|


### math.add
```
Description: Formula to perform an addition
Subroutine Syntax: {% raw %}{{*math.add(X,Y)}}{% endraw %}

Example:
{% raw %}{{*math.add(1,1)}}{% endraw %}

Example Output:
2
```
### math.divide
```
Description: Formula to perform a division
Subroutine Syntax: {% raw %}{{*math.divide(X,Y)}}{% endraw %}

Example:
{% raw %}{{*math.divide(4,2)}}{% endraw %}

Example Output:
2.00
```
### math.power
```
Description: Formula to perform an exponentiation
Subroutine Syntax: {% raw %}{{*math.power(X,Y)}}{% endraw %}

Example:
{% raw %}{{*math.power(3,2)}}{% endraw %}

Example Output:
9
```
### math.multiply
```
Description: Formula to perform a mutiplication
Subroutine Syntax: {% raw %}{{*math.multiply(X,Y)}}{% endraw %}

Example:
{% raw %}{{*math.multiply(3,4)}}{% endraw %}

Example Output:
12
```
### math.substract
```
Description: Formula to perform a substraction
Subroutine Syntax: {% raw %}{{*math.substract(X,Y)}}{% endraw %}

Example:
{% raw %}{{*math.substract(12,4)}}{% endraw %}

Example Output:
8
```
