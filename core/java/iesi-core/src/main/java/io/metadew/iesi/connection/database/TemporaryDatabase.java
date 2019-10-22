package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.TemporaryDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class TemporaryDatabase extends Database {


    public TemporaryDatabase(TemporaryDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public String getSystemTimestampExpression() {
        return null;
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras() {
        return "";
    }

    @Override
    public boolean addComments() {
        return false;
    }

    @Override
    public String toQueryString(MetadataField field) {
        return "";
    }

}