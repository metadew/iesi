{% include navigation.html %}

# Connection types

Connection types are the reusable configuration blocks. 
Each type allows the framework to connect to specific technology and requires a different set of input parameters. 

The connection types are prefix based on a connectivity category:

|Prefix|Category|
|---|---|
|db|Database connectivity|
|fwk|Framework capabilities|
|host|Operating system connectivity|
|http|Http-based connectivity|
|repo|Repository connectivity|

The table provides an overview of all connection types. 
Additional details on the parameters per connection type are provided below.

<table>
<colgroup>
<col width="30%" />
<col width="70%" />
</colgroup>
<thead>
<tr class="header">
<th>Connection Type</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.ConnectionTypes %}
<tr>
<td markdown="span">[{{ type.data.name }}](/{{site.repository}}/pages/define/connectiontypes/{{ type.data.name }}.html)</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

{% for detail in site.data.ConnectionTypes %}
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
