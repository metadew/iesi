{% include navigation.html %}

# Catalog tables

The table provides an overview of all catalog tables. 

<table>
<thead>
<tr class="header">
<th>Label</th>
<th>Name</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.datamodel.CatalogTables %}
<tr>
<td markdown="span">[{{ type.data.label }}](/{{site.repository}}/pages/understand/datamodel/connectivity/{{ type.data.label }}.html)</td>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>