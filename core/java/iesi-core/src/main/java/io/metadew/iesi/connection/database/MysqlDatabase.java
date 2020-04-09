package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mysql.MysqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class MysqlDatabase extends Database {


    public MysqlDatabase(MysqlDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
