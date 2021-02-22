## Connection types
{% include navigation.html %}

Connection types are the reusable configuration blocks. 
Each type allows the framework to connect to specific technology and requires a different set of input parameters. 

The connection types are prefix based on a connectivity category:

|Prefix|Category|
|---|---|
|db|Database connectivity|
|fwk|Framework connectivity|
|host|Operating system connectivity|
|http|Http-based connectivity|
|repo|Repository connectivity|
|socket| Socket connectivity|

The table provides an overview of all connection types. 
Additional details on the parameters per connection type are provided below.


|Category|Type|Description            |
|--------|----|-----------------------|
|**db**|
|      |db.db2|IBM Database Connection|
|      |db.dremio|Dremio Database Connection|
|      |db.drill|Apache Drill Database Connection|
|      |db.h2|H2 Database Connection|
|      |db.mariadb|MariaDB Database Connection|
|      |db.mssql|Microsoft SQL Database Connection|
|      |db.mysql|MySql Database Connection|
|      |db.netezza|Netezza Database Connection|
|      |db.oracle|Oracle Database Connection|
|      |db.postgresql|Postgresql Database Connection|
|      |db.presto|Presto Database Connection|
|      |db.sqlite|SQLite Database Connection|
|      |db.teradata|Teradata Database Connection|
|**fwk**|
|      |fwk.alias|Connection alias only to be used for impersonation|
|**host**|
|      |host.linux|Linux-Based Operating System Connection|
|      |host.unix|Unix-Based Operating System Connection|
|      |host.windows|Windows-Based Operating System Connection|
|**http**|
|      |http|Http Host Connection|
|**repo**|
|      |repo.artifactory|Artifactory Repository Connection|
|**socket**|
|      |socket|Socket Connection|
