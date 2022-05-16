{% include navigation.html %}

# Generate - Text Instructions
## Overview
This page contains all information on generating and formatting of string - xml - json responses

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |text.replace|Formula to replace characters|
|* |text substring|Formula to fetch text based on start- and endposition|
|* |text.uuid|Formula to generate random uuid|
|* |text.xmlpath|Formula to fetch an element from an XML response|
|* |text.jsonpath|Formula to fetch an element from an JSON response|


### text.replace
```
Description: Formula to replace characters
Subroutine Syntax: {% raw %}{{*text.replace("X","Y","Z")}}{% endraw %}
  <X>: text
  <Y>: character to be replaced
  <Z>: replacement character

Example 1:
{% raw %}{{*text.replace("1.00",".",",")}}{% endraw %}

Example 1 Output:
1,00

Example 2:
{% raw %}{{*text.replace("euro","EUR",)}}{% endraw %}

Example 2 Output:
EUR

Example 3:
{% raw %}{{*text.replace("#variable#",",",".")}}{% endraw %}
{% raw %}{{*text.replace("{{=dataset(Outputdataset,Test)}}",",",".")}}{% endraw %}

Example 3 Output:
1,00
```
### text substring
```
Description: Formula to fetch text based on start- and endposition
Subroutine Syntax: {% raw %}{{*text.substring(X,Y,Z)}}{% endraw %}
  <X>: text
  <Y>: start position
  <Z>: end position

Example:
{% raw %}{{*text.substring(Hello World,1,5)}}{% endraw %}

Example Output:
Hello
```
### text.uuid
```
Description: Formula to generate random uuid
Subroutine Syntax: {% raw %}{{*text.uuid()}}{% endraw %}

Example:
{% raw %}{{*text.uuid()}}{% endraw %}

Example Output:
a92a8d79-2072-43eb-b2b4-5d45819a6671
```
### text.xmlPath
```
Description: Formula to fetch an element from an XML response
Subroutine Syntax: {% raw %}{{*text.xlmPath(X,Y)}}{% endraw %}
  <X>: text
  <Y>: path to the element

Example 1:
{% raw %}{{*text.xmlPath(<sum><param1>1</param1><param2>2</param2><expected>3</expected></sum>,/sum/expected)}}{% endraw %}

Example 1 Output:
3 (the expected value)

Example 2:
{% raw %}{{*text.xmlPath({{=dataset(Outputdataset,body)}},/sum/expected[1]/id)}}{% endraw %}

Example 2 Output:
5 (the first element in the expected value elementlist)
```
### text.jsonPath
```
Description: Formula to fetch an element from an JSON response
Subroutine Syntax: {% raw %}{{*text.jsonPath(X,Y)}}{% endraw %}
  <X>: text
  <Y>: path to the element

Example 1:
{% raw %}{{*text.jsonPath({"sub":{"param1":6,"param2":3,"expected":3}},/sub/expected)}}{% endraw %}

Example 1 Output:
3 (the expected value)

Example 2:
{% raw %}{{*text.jsonPath({{=dataset(OutputDataset, body)}}, /responseSets/0/responses/0/id)}}{% endraw %}

Example 2 Output:
5 (the first element in the expected value elementlist)
```
