{% include navigation.html %}

# Action types

Action types are the reusable building blocks of automation scripts. 
Each type performs a specific operations and requires a different set of input parameters. 

The action types are prefix based on a action category:

|Prefix|Category|
|---|---|
|cli|Command line instructions|
|conn|Connectivity operations|
|data|Data related operations|
|eval|Evaluation operations|
|fho|File handling operations|
|fwk|Framework operations|
|http|Http-based operations|
|sql|Database SQL operations|
|wfa|Wait for activity operations|

The table provides an overview of all action types. 
Additional details on the parameters per action type are provided below.

<table>
<colgroup>
<col width="30%" />
<col width="10%" />
<col width="60%" />
</colgroup>
<thead>
<tr class="header">
<th>Action Type</th>
<th>Status</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.ActionTypes %}
<tr>
<td markdown="span">[{{ type.data.name }}](/{{site.repository}}/pages/design/actiontypes/{{ type.data.name }}.html)</td>
<td markdown="span">{{ type.data.status }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

For more information on the status definition, please refer to the [legend](/{{site.repository}}/pages/understand/legend.html) page.

{% for detail in site.data.ActionTypes %}
## [{{ detail.data.name }}](/{{site.repository}}/pages/design/actiontypes/{{ detail.data.name }}.html)

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
