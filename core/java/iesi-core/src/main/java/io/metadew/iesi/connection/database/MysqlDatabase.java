package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.MysqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class MysqlDatabase extends Database {


    public MysqlDatabase(MysqlDatabaseConnection databaseConnection) {
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
