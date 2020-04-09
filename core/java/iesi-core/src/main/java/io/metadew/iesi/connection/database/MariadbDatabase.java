package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mariadb.MariadbDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

public class MariadbDatabase extends Database {


    public MariadbDatabase(MariadbDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
