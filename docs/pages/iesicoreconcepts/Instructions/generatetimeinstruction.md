{% include navigation.html %}

# Generate - Time Instructions
## Overview
This page contains all information on generating and formatting of time values

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |time.format|Format a timestamp|
|* |time.now|Generates the current timestamp|
|* |time.travel|Generates a timestamp in the future with nr of hours, minutes, seconds|
|* |time.travel|Generates a timestamp in the past with nr of hours, minutes, seconds|

### time.format
```
Description: Format a timestamp
Subroutine Syntax: {% raw %}{{*time.format(X,Y)}}{% endraw %}
  <X>: original format
  <Y>: new format

Example 1:
{% raw %}{{*time.format({{*time.now()}},yyyyMMddHHmmss)}}{% endraw %}

Example 1 Output:
20210402093846 

Example 2:
{% raw %}{{*time.format({{*time.now()}},yyyyMMddHHmm)}}{% endraw %}

Example 2 Output:
202104020938

Example 3:
{% raw %}{{*time.format({{*time.now()}},yyyy-MM-dd HH:mm:ss.SSS)}}{% endraw %}

Example 3 Output:
2021-04-02 09:38:46.106
```
### time.now
```
Description: Generates the current timestamp
Subroutine Syntax: {% raw %}{{*time.now()}}{% endraw %} - Format: yyyy-MM-dd HH:mm:ss.SSS

Example:
{% raw %}{{*time.now()}}{% endraw %}

Example Output:
2021-04-02 09:38:46.106
```
### time.travel
```
Description: Generates a timestamp in the future with nr of hours, minutes, seconds
Subroutine Syntax: {% raw %}{{*time.travel(X,Y,Z)}}{% endraw %}
  <X>: time which you would like to travel from
  <Y>: travel in hours, minutes, seconds
  <Z>: amount of hours, minutes, seconds
  
Example 1:
{% raw %}{{*time.travel ({{*time.now()}},”hour”,5)}}{% endraw %}

Example 1 Output:
2021-04-02 14:38:46.106 

Example 2:
{% raw %}{{*time.travel ({{*time.now()}},”minute”,5)}}{% endraw %}

Example 2 Output:
2021-04-02 09:43:46.106

Example 3:
{% raw %}{{*time.travel ({{*time.now()}},”second”,5)}}{% endraw %}

Example 3 Output:
2021-04-02 09:38:51.106
```
### time.travel
```
Description: Generates a timestamp in the past with nr of hours, minutes, seconds
Subroutine Syntax: {% raw %}{{*time.travel(X,Y,-Z)}}{% endraw %}
  <X>: time which you would like to travel from
  <Y>: travel in hours, minutes, seconds
  <-Z>: amount of hours, minutes, seconds
  
Example 1:
{% raw %}{{*time.travel ({{*time.now()}},”hour”,-5)}}{% endraw %}

Example 1 Output:
2021-04-02 04:38:46.106 

Example 2:
{% raw %}{{*time.travel ({{*time.now()}},”minute”,-5)}} {% endraw %}

Example 2 Output:
2021-04-02 09:33:46.106 

Example 3:
{% raw %}{{*time.travel ({{*time.now()}},”second”,-5)}}{% endraw %}

Example 3 Output:
2021-04-02 09:38:41.106
```
