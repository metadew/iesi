{% include navigation.html %}
## Connection types
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
|      |[db.db2](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.db2.html)|IBM Database Connection|
|      |[db.dremio](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.dremio.html)|Dremio Database Connection|
|      |[db.drill](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.drill.html)|Apache Drill Database Connection|
|      |[db.h2](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.h2.html)|H2 Database Connection|
|      |[db.mariadb](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.mariadb.html)|MariaDB Database Connection|
|      |[db.mssql](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.mssql.html)|Microsoft SQL Database Connection|
|      |[db.mysql](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.mysql.html)|MySql Database Connection|
|      |[db.netezza](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.netezza.html)|Netezza Database Connection|
|      |[db.oracle](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.oracle.html)|Oracle Database Connection|
|      |[db.postgresql](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.postgresql.html)|Postgresql Database Connection|
|      |[db.presto](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.presto.html)|Presto Database Connection|
|      |[db.sqlite](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.sqlite.html)|SQLite Database Connection|
|      |[db.teradata](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/db.teradata.html)|Teradata Database Connection|
|**fwk**|
|      |[fwk.alias](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/fwk.alias.html)|Connection alias only to be used for impersonation|
|**host**|
|      |[host.linux](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/host.linux.html)|Linux-Based Operating System Connection|
|      |[host.unix](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/host.unix.html)|Unix-Based Operating System Connection|
|      |[host.windows](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/host.windows.html)|Windows-Based Operating System Connection|
|**http**|
|      |[http](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/http.html)|Http Host Connection|
|**repo**|
|      |[repo.artifactory](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/repo.artifactory.html)|Artifactory Repository Connection|
|**socket**|
|      |[socket](/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/socket.html)|Socket Connection|
