{% include navigation.html %}

# Subroutine types

Subroutine types are the reusable building blocks of action type parameter values. 
Each type allows the framework to use them for specific action type parameters and requires a different set of input parameters. 

The subroutine types are prefix based on a action category:

|Prefix|Category|
|---|---|
|cli|Command line parameters|
|sql|Database SQL parameters|

The table provides an overview of all subroutine types. 
Additional details on the parameters per subroutine type are provided below.

<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Subroutine Type</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.SubroutineTypes %}
<tr>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

{% for detail in site.data.SubroutineTypes %}
## {{ detail.data.name }}

Description: {{ detail.data.description }}

<table>
<colgroup>
<col width="20%" />
<col width="40%" />
<col width="20%" align="center"/>
<col width="20%" align="center"/>
</colgroup>
<thead>
<tr class="header">
<th>Parameter</th>
<th>Description</th>
<th>Type</th>
<th>Mandatory</th>
</tr>
</thead>
<tbody>
{% assign params = detail.data.parameters %}
{% for param in params %}
<tr>
<td markdown="span">{{ param.name }}</td>
<td markdown="span">{{ param.description }}</td>
<td markdown="span">{{ param.type }}</td>
<td markdown="span">{{ param.mandatory }}</td>
</tr>
{% endfor %}
</tbody>
</table>

{% endfor %}
