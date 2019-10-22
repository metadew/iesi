package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mariadb.MariadbDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

public class MariadbDatabase extends Database {


    public MariadbDatabase(MariadbDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return null;
    }

    @Override
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        return null;
    }

    @Override
    public String createQueryExtras() {
        return null;
    }

    @Override
    public boolean addComments() {
        return false;
    }

    @Override
    public String toQueryString(MetadataField field) {
        return null;
    }

}
