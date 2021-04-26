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
|      |[db.db2]((/{{site.repository}}/pages/iesicoreconcepts/ConnectionTypes/dbdb2.html)|IBM Database Connection|
|      |[db.dremio](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.dremio.md)|Dremio Database Connection|
|      |[db.drill](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.drill.md)|Apache Drill Database Connection|
|      |[db.h2](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.h2.md)|H2 Database Connection|
|      |[db.mariadb](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.mariadb.md)|MariaDB Database Connection|
|      |[db.mssql](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.mssql.md)|Microsoft SQL Database Connection|
|      |[db.mysql](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.mysql.md)|MySql Database Connection|
|      |[db.netezza](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.netezza.md)|Netezza Database Connection|
|      |[db.oracle](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.oracle.md)|Oracle Database Connection|
|      |[db.postgresql](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.postgresql.md)|Postgresql Database Connection|
|      |[db.presto](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.presto.md)|Presto Database Connection|
|      |[db.sqlite](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.sqlite.md)|SQLite Database Connection|
|      |[db.teradata](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/db.teradata.md)|Teradata Database Connection|
|**fwk**|
|      |[fwk.alias](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/fwk.alias.md)|Connection alias only to be used for impersonation|
|**host**|
|      |[host.linux](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/host.linux.md)|Linux-Based Operating System Connection|
|      |[host.unix](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/host.unix.md)|Unix-Based Operating System Connection|
|      |[host.windows](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/host.windows.md)|Windows-Based Operating System Connection|
|**http**|
|      |[http](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/http.md)|Http Host Connection|
|**repo**|
|      |[repo.artifactory](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/repo.artifactory.md)|Artifactory Repository Connection|
|**socket**|
|      |[socket](https://github.com/metadew/iesi/blob/2bf8147b794d0c7bebd8a1f83f566d77200625b8/docs/pages/iesi%20core%20concepts/Connection%20Types/socket.md)|Socket Connection|
