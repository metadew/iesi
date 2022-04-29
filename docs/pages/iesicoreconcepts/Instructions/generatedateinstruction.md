{% include navigation.html %}


# Generate - Date Instructions
## Overview
This page contains all information on generating and formatting of date values

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |date.between|Get a random date between a defined range in format ddMMyyyy|
|* |date.format|Format a date|
|* |date.today|Generates today’s date|
|* |date.travel|Generates a date in the future with nr of days, months, years|
|* |date.travel|Generates a date in the past with nr of days, months, years|
|* |date.travel|Generates a date in the future/past with only weekdays (weekends excl.)|

### date.between
```
Description: Get a random date between a defined range in format ddMMyyyy
Subroutine Syntax: {% raw %}{{*date.between(ddMMyyyy,ddMMyyyyy)}}{% endraw %}

Example:
{% raw %}{{*date.between(30111995,01012000)}}{% endraw %}

Example Output:
16101996
```
### date.format
```
Description: Format a date
Subroutine Syntax: {% raw %}{{*date.format(X,Y)}}{% endraw %}
  <X>: original format
  <Y>: new format

Example 1:
{% raw %}{{*date.format(30111995, yyyyMMdd)}}{% endraw %}

Example 1 Output:
19951130

Example 2:
{% raw %}{{*date.format({{*date.today()}},yyyyMMdd)}}{% endraw %}

Example 2 Output:
20210402

Example 3:
{% raw %}{{*date.format({{*date.today()}},dd/MM/yyyy)}}{% endraw %}

Example 3 Output:
02/04/2021
```
### date.today
```
Description: Generates today’s date
Subroutine Syntax: {% raw %}{{*date.today()}} - format: ddMMyyyy{% endraw %}

Example:
{% raw %}{{*date.today()}}{% endraw %}

Example Output:
02042021
```
### date.travel
```
Description: Generates a date in the future with nr of days, months, years
Subroutine Syntax: {% raw %}{{*date.travel(X,Y,Z)}}{% endraw %}
  <X>: date which you would like to travel from
  <Y>: travel in days, months, years
  <Z>: amount of days, months, years
  
Example 1:
{% raw %}{{*date.travel({{*date.today()}},”day”,1}}{% endraw %}

Example 1 Output:
03042021

Example 2:
{% raw %}{{*date.travel({{*date.today()}},”month”,1}}{% endraw %}

Example 2 Output:
02052021

Example 3:
{% raw %}{{*date.travel({{*date.today()}},”year”,1}}{% endraw %}

Example 3 Output:
02042022
```
### date.travel
```
Description: Generates a date in the future with nr of days, months, years
Subroutine Syntax: {% raw %}{{*date.travel(X,Y,-Z)}}{% endraw %}
  <X>: date which you would like to travel from
  <Y>: travel in days, months, years
  <-Z>: amount of days, months, years
  
Example 1:
{% raw %}{{*date.travel({{*date.today()}},”day”,-1}}{% endraw %}

Example 1 Output:
01042021

Example 2:
{% raw %}{{*date.travel({{*date.today()}},”month”,-1}}{% endraw %}

Example 2 Output:
01032021

Example 3:
{% raw %}{{*date.travel({{*date.today()}},”year”,-1}}{% endraw %}

Example 3 Output:
01032020
```
### date.travel
```
Description: Generates a date in the future/past with only weekdays (weekends excl.)
Subroutine Syntax: {% raw %}{{*date.travel(X,Y,Z,W)}}{% endraw %}
  <X>: date which you would like to travel from
  <Y>: travel in days
  <Z>: amount of days
  <W>: only weekdays
  
Example:
{% raw %}{{*date.travel({{*date.today()}},”day”,2,W)}}{% endraw %}

Example Output:
D+2 weekdays
```
