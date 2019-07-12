{% include navigation.html %}

# Connectors

The automation framework can connect to different data sources. If possible the necessary libraries are included in the distribution. 
But sometimes this is not possible due to licensing restrictions. 
The table below indicates if the library is included in the distribution or not. 


To include the library, simply copy the `driver.jar` file into the `lib` folder. 
The framework will load all necessary driver on startup.

*Note:* the same applies if you would like to upgrade a version of the library in line with your needs. 

|Technology|Library|Comment|
|----------|    :---:    |   :---:    |
|Apache Drill|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Dremio|![red](/{{site.repository}}/images/icons/red-dot.png))||
|File System|![grey](/{{site.repository}}/images/icons/green-dot.png)||
|H2|![red](/{{site.repository}}/images/icons/red-dot.png))||
|IBM DB2|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Maria DB|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Microsoft SQL Server|![red](/{{site.repository}}/images/icons/red-dot.png)||
|MySQL|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Netezza|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Oracle|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Postgresql|![red](/{{site.repository}}/images/icons/red-dot.png)||
|Presto|![red](/{{site.repository}}/images/icons/red-dot.png)||
|SQLite|![green](/{{site.repository}}/images/icons/green-dot.png)||
|Teradata|![red](/{{site.repository}}/images/icons/red-dot.png)||

*Legend*

|Color|Description|
|:---:|:---:|
|![green](/{{site.repository}}/images/icons/green-dot.png)|Included|
|![yellow](/{{site.repository}}/images/icons/yellow-dot.png)|In progress|
|![red](/{{site.repository}}/images/icons/red-dot.png)|Not included|
|![grey](/{{site.repository}}/images/icons/grey-dot.png)|Not applicable|