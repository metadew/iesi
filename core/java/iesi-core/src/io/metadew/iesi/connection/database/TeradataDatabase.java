package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.TeradataDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class TeradataDatabase extends Database {


    public TeradataDatabase(TeradataDatabaseConnection databaseConnection) {
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
