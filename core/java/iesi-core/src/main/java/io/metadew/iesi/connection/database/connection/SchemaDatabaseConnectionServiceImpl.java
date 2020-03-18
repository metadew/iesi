package io.metadew.iesi.connection.database.connection;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class SchemaDatabaseConnectionServiceImpl<T extends SchemaDatabaseConnection> extends DatabaseConnectionServiceImpl<T> implements SchemaDatabaseConnectionService<T> {


}