{% include navigation.html %}

# Repository settings

The metadata repository is configured in the `application-repository.yml` file. This file defines the database(s) to use to store all the metadata

## Concepts
* Categories: the metadata stored by IESI can be categorized into 5 categories: connectivity, design, execution request, result and trace. Each of these categories can have a dedicated database.
* Coordinator: if it is desirable to use a different database user for DDL and DML scripts it is possible to define an owner database user, a writer database user and a reader database user. The owner should have permissions to execute DDL operations, the writer should have write permissions and the reader should have read permissions on the necessary tables.

## Configuration
The metadata repository configuration is defined under the `iesi.metadata.repository` key. It defines a list of metadata repositories.  

A metadata repository configuration contains two mandatory parameters:
* `categories`: a list defining the categories this definition is responsible for.
* `coordinator`: a coodinator definition. This configuration determines
  * the type of database used
  * the jdbc connection url used
  * the database user credentials to use (for owner, writer and reader)

The example below defines a metadata repository responsible for all categories of metadata using a H2 database hosted on a file (`~/repository`) and the database user `sa`.
```yaml
iesi:
  metadata:
    repository:
      - categories:
          - general
        coordinator:
          type: h2
          connection: jdbc:h2:~/repository
          owner:
            user: "sa"
            password: ""
```

### Metdata Repository Database types
Currently IESI supports the following database technologies as metadata repositories:
* H2
* SQLite
* MsSql
* MySql
* Netezza
* PostgreSQL

### Advanced configuration

You can provide an SQL query that will be executed first when opening a JDBC connection to the database using the `init_sql` parameter.

```yaml
...
        coordinator:
          type: h2
          init_sql: alter session set ...
          connection: jdbc:h2:~/repository
...
```
