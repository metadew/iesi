{% include navigation.html %}

# Repository settings

The repository settings file defines the necessary values for the defined repository category.
* The settings file syntax is `keyvalue`
* The settings file is required to be loaded in order for the framework to work correctly

## Repository definition

### 1: iesi.metadata.repository.name

`iesi.metadata.repository.name=<name>`
* define the name for the repository
* this name will be used as reference by the framework and needs to be unique
* use of spaces and special characters is not advised
* use of upper- and lowercase characters does not have an impact

### 2: iesi.metadata.repository.type

`iesi.metadata.repository.type=<type>`
* define the type of the repository
* this refers to the technology that is used
* it drives the selection of the settings in the respective sub-section of the setting file
* options are listed below as part of the supported types

### 3: iesi.metadata.repository.category

`iesi.metadata.repository.category=<category>`
* define the category of the repository
* the category of the repository refers to the configuration data that is managed by the repository
* the framework can be deployed to host one, more or all categories together or separate them. This is typically done to
  * increase security for connectivity data
  * increase distribution for design data
  * scale and distribute result data
  * ...
* options are: general, connectivity, control, design, result, trace

### 4: iesi.metadata.repository.scope

`iesi.metadata.repository.scope=<scope>`
* define the scope of the repository
* optional setting (placeholder for future enhancements)

**Work in progress**

### 5: iesi.metadata.repository.instance

`iesi.metadata.repository.instance=<instance>`
* define the instance of the repository
* each repository can be instantiated logically separate within a physical instance
* practically this means that the data structures are dynamically prefixed with the instance name

## Supported types

### General settings

### 1: iesi.metadata.repository.connection.string

`iesi.metadata.repository.connection.string=<connectionurl>`
* define the connection string for the repository connection
* regardless of the supported type a connection string can be entered custom that will overwrite the framework's attempt to build it itself
* this is useful with very specific settings that need to be provided
* note that user and password information is still required in the appropriate type sub-section

### filestore

### 1: iesi.metadata.repository.filestore.path

`iesi.metadata.repository.filestore.path=<path>`
* define the path for the repository connection

**Work in progress**

### h2

### 1: iesi.metadata.repository.h2.host

`iesi.metadata.repository.h2.host=<host>`
* define the host name for the repository connection
* this parameter is optional and only required with the h2 repository is running in server mode

### 2: iesi.metadata.repository.h2.port

`iesi.metadata.repository.h2.port=<port>`
* define the port number for the repository connection
* this parameter is optional and only required with the h2 repository is running in server mode

### 3: iesi.metadata.repository.h2.file

`iesi.metadata.repository.h2.file=<file>`
* define the file name for the repository connection

### 4: iesi.metadata.repository.h2.schema

`iesi.metadata.repository.h2.schema=<schema>`
* define the schema name for the repository connection

### 5: iesi.metadata.repository.h2.owner

`iesi.metadata.repository.h2.owner=<owner>`
* define the owner user

### 6: iesi.metadata.repository.h2.owner.password

`iesi.metadata.repository.h2.owner.password=<password>`
* define the password for the owner user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 7: iesi.metadata.repository.h2.writer

`iesi.metadata.repository.h2.writer=<writer>`
* define the writer user

### 8: iesi.metadata.repository.h2.writer.password

`iesi.metadata.repository.h2.writer.password=<password>`
* define the password for the writer user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 9: iesi.metadata.repository.h2.reader

`iesi.metadata.repository.h2.reader=<reader>`
* define the reader user

### 10: iesi.metadata.repository.h2.reader.password

`iesi.metadata.repository.h2.reader.password=<password>`
* define the password for the reader user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### elasticsearch

### 1: iesi.metadata.repository.elasticsearch.url

`iesi.metadata.repository.elasticsearch.url=<url>`
* define the url for the repository connection

**Work in progress**

### mssql

### 1: iesi.metadata.repository.mssql.host

`iesi.metadata.repository.mssql.host=<host>`
* define the host name for the repository connection

### 2: iesi.metadata.repository.mssql.port

`iesi.metadata.repository.mssql.port=<port>`
* define the port number for the repository connection

### 3: iesi.metadata.repository.mssql.name

`iesi.metadata.repository.mssql.name=<name>`
* define the name for the repository connection

### 4: iesi.metadata.repository.mssql.database

`iesi.metadata.repository.mssql.database=<database>`
* define the database name for the repository connection

### 5: iesi.metadata.repository.mssql.schema

`iesi.metadata.repository.mssql.schema=<schema>`
* define the schema name for the repository connection

### 6: iesi.metadata.repository.mssql.owner

`iesi.metadata.repository.mssql.owner=<owner>`
* define the owner user

### 7: iesi.metadata.repository.mssql.owner.password

`iesi.metadata.repository.mssql.owner.password=<password>`
* define the password for the owner user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 8: iesi.metadata.repository.mssql.writer

`iesi.metadata.repository.mssql.writer=<writer>`
* define the writer user

### 9: iesi.metadata.repository.mssql.writer.password

`iesi.metadata.repository.mssql.writer.password=<password>`
* define the password for the writer user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 10: iesi.metadata.repository.mssql.reader

`iesi.metadata.repository.mssql.reader=<reader>`
* define the reader user

### 11: iesi.metadata.repository.mssql.reader.password

`iesi.metadata.repository.mssql.reader.password=<password>`
* define the password for the reader user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### netezza

### 1: iesi.metadata.repository.netezza.host

`iesi.metadata.repository.netezza.host=<host>`
* define the host name for the repository connection

### 2: iesi.metadata.repository.netezza.port

`iesi.metadata.repository.netezza.port=<port>`
* define the port number for the repository connection

### 3: iesi.metadata.repository.netezza.name

`iesi.metadata.repository.netezza.name=<name>`
* define the name for the repository connection

### 4: iesi.metadata.repository.netezza.schema

`iesi.metadata.repository.netezza.schema=<schema>`
* define the schema name for the repository connection

### 5: iesi.metadata.repository.netezza.owner

`iesi.metadata.repository.netezza.owner=<owner>`
* define the owner user

### 6: iesi.metadata.repository.netezza.owner.password

`iesi.metadata.repository.netezza.owner.password=<password>`
* define the password for the owner user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 7: iesi.metadata.repository.netezza.writer

`iesi.metadata.repository.netezza.writer=<writer>`
* define the writer user

### 8: iesi.metadata.repository.netezza.writer.password

`iesi.metadata.repository.netezza.writer.password=<password>`
* define the password for the writer user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 9: iesi.metadata.repository.netezza.reader

`iesi.metadata.repository.netezza.reader=<reader>`
* define the reader user

### 10: iesi.metadata.repository.netezza.reader.password

`iesi.metadata.repository.netezza.reader.password=<password>`
* define the password for the reader user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### oracle

### 1: iesi.metadata.repository.oracle.host

`iesi.metadata.repository.oracle.host=<host>`
* define the host name for the repository connection

### 2: iesi.metadata.repository.oracle.port

`iesi.metadata.repository.oracle.port=<port>`
* define the port number for the repository connection

### 3: iesi.metadata.repository.oracle.name

`iesi.metadata.repository.oracle.name=<name>`
* define the name for the repository connection

### 4: iesi.metadata.repository.oracle.tnsalias

`iesi.metadata.repository.oracle.tnsalias=<tnsalias>`
* define the tnsalias name for the repository connection
* this setting is only used when no service name is defined

### 5: iesi.metadata.repository.oracle.service

`iesi.metadata.repository.oracle.service=<service>`
* define the service name for the repository connection
* this settings is always used if defined

### 6: iesi.metadata.repository.oracle.schema

`iesi.metadata.repository.oracle.schema=<schema>`
* define the schema name for the repository connection

### 7: iesi.metadata.repository.oracle.owner

`iesi.metadata.repository.oracle.owner=<owner>`
* define the owner user

### 8: iesi.metadata.repository.oracle.owner.password

`iesi.metadata.repository.oracle.owner.password=<password>`
* define the password for the owner user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 9: iesi.metadata.repository.oracle.writer

`iesi.metadata.repository.oracle.writer=<writer>`
* define the writer user

### 10: iesi.metadata.repository.oracle.writer.password

`iesi.metadata.repository.oracle.writer.password=<password>`
* define the password for the writer user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 11: iesi.metadata.repository.oracle.reader

`iesi.metadata.repository.oracle.reader=<reader>`
* define the reader user

### 12: iesi.metadata.repository.oracle.reader.password

`iesi.metadata.repository.oracle.reader.password=<password>`
* define the password for the reader user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### postgresql

### 1: iesi.metadata.repository.postgresql.host

`iesi.metadata.repository.postgresql.host=<host>`
* define the host name for the repository connection

### 2: iesi.metadata.repository.postgresql.port

`iesi.metadata.repository.postgresql.port=<port>`
* define the port number for the repository connection

### 3: iesi.metadata.repository.postgresql.name

`iesi.metadata.repository.postgresql.name=<name>`
* define the name for the repository connection

### 4: iesi.metadata.repository.postgresql.schema

`iesi.metadata.repository.postgresql.schema=<schema>`
* define the schema name for the repository connection

### 5: iesi.metadata.repository.postgresql.owner

`iesi.metadata.repository.postgresql.owner=<owner>`
* define the owner user

### 6: iesi.metadata.repository.postgresql.owner.password

`iesi.metadata.repository.postgresql.owner.password=<password>`
* define the password for the owner user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 7: iesi.metadata.repository.postgresql.writer

`iesi.metadata.repository.postgresql.writer=<writer>`
* define the writer user

### 8: iesi.metadata.repository.postgresql.writer.password

`iesi.metadata.repository.postgresql.writer.password=<password>`
* define the password for the writer user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### 9: iesi.metadata.repository.postgresql.reader

`iesi.metadata.repository.postgresql.reader=<reader>`
* define the reader user

### 10: iesi.metadata.repository.postgresql.reader.password

`iesi.metadata.repository.postgresql.reader.password=<password>`
* define the password for the reader user
* the password can be encrypted by the framework. More information can be found [here](/{{site.repository}}/pages/operate/operate.html)

### sqlite

### 1: iesi.metadata.repository.sqlite.file

`iesi.metadata.repository.sqlite.file=<path>`
* define the file name (including path) for the repository connection
