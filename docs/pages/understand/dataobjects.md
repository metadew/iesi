{% include navigation.html %}

# Data objects

The framework is based on a set of data objects that work closely together.

## Concept

* Our approach positions itself as a metadata framework where **automation scripts are configured** rather than coded.
* Common actions and components are managed in **libraries** that are maintained centrally and that are reused to design scripts
* It is a **toolbelt** rather than a one-stop solution: quick to extend with new functions, effective in growing the coverage of automation
* The framework **decouples configuration from data** aspects and manages it in a distributed manner

![objects-concept](/{{site.repository}}/images/understand/objects-concept.png)

![objects-concept-legend](/{{site.repository}}/images/understand/objects-concept-legend.png)

## Objects

### Connectivity

<table>
<thead>
<tr class="header">
<th>Label</th>
<th>Name</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.datamodel.ConnectivityObjects %}
<tr>
<td markdown="span">[{{ type.data.label }}](/{{site.repository}}/pages/understand/dataobjects/{{ type.data.label | downcase }}.html)</td>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

### Design

<table>
<thead>
<tr class="header">
<th>Label</th>
<th>Name</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.datamodel.DesignObjects %}
<tr>
<td markdown="span">[{{ type.data.label }}](/{{site.repository}}/pages/understand/dataobjects/{{ type.data.label | downcase }}.html)</td>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

### Result

<table>
<thead>
<tr class="header">
<th>Label</th>
<th>Name</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.datamodel.ResultObjects %}
<tr>
<td markdown="span">[{{ type.data.label }}](/{{site.repository}}/pages/understand/dataobjects/{{ type.data.label | downcase }}.html)</td>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>

### Trace

<table>
<thead>
<tr class="header">
<th>Label</th>
<th>Name</th>
<th>Description</th>
</tr>
</thead>
<tbody>
{% for type in site.data.datamodel.TraceObjects %}
<tr>
<td markdown="span">[{{ type.data.label }}](/{{site.repository}}/pages/understand/dataobjects/{{ type.data.label | downcase }}.html)</td>
<td markdown="span">{{ type.data.name }}</td>
<td markdown="span">{{ type.data.description }}</td>
</tr>
{% endfor %}
</tbody>
</table>
