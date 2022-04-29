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
Subroutine Syntax: {{*date.between(ddMMyyyy,ddMMyyyyy)}}

Example:
{{*date.between(30111995,01012000)}}

Example Output:
16101996
```
### date.format
```
Description: Format a date
Subroutine Syntax: {{*date.format(X,Y)}}
  <X>: original format
  <Y>: new format

Example 1:
{{*date.format(30111995, yyyyMMdd)}}

Example 1 Output:
19951130

Example 2:
{{*date.format({{*date.today()}},yyyyMMdd)}}

Example 2 Output:
20210402

Example 3:
{{*date.format({{*date.today()}},dd/MM/yyyy)}}

Example 3 Output:
02/04/2021
```
### date.today
```
Description: Generates today’s date
Subroutine Syntax: {{*date.today()}} - format: ddMMyyyy

Example:
{{*date.today()}}

Example Output:
02042021
```
### date.travel
```
Description: Generates a date in the future with nr of days, months, years
Subroutine Syntax: {{*date.travel(X,Y,Z)}}
  <X>: date which you would like to travel from
  <Y>: travel in days, months, years
  <Z>: amount of days, months, years
  
Example 1:
{{*date.travel({{*date.today()}},”day”,1}}

Example 1 Output:
03042021

Example 2:
{{*date.travel({{*date.today()}},”month”,1}}

Example 2 Output:
02052021

Example 3:
{{*date.travel({{*date.today()}},”year”,1}}

Example 3 Output:
02042022
```
### date.travel
```
Description: Generates a date in the future with nr of days, months, years
Subroutine Syntax: {{*date.travel(X,Y,-Z)}}
  <X>: date which you would like to travel from
  <Y>: travel in days, months, years
  <-Z>: amount of days, months, years
  
Example 1:
{{*date.travel({{*date.today()}},”day”,-1}}

Example 1 Output:
01042021

Example 2:
{{*date.travel({{*date.today()}},”month”,-1}}

Example 2 Output:
01032021

Example 3:
{{*date.travel({{*date.today()}},”year”,-1}}

Example 3 Output:
01032020
```
### date.travel
```
Description: Generates a date in the future/past with only weekdays (weekends excl.)
Subroutine Syntax: {{*date.travel(X,Y,Z,W)}}
  <X>: date which you would like to travel from
  <Y>: travel in days
  <Z>: amount of days
  <W>: only weekdays
  
Example:
{{*date.travel({{*date.today()}},”day”,2,W)}}

Example Output:
D+2 weekdays
```
