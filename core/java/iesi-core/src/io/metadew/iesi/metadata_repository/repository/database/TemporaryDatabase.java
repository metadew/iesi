package io.metadew.iesi.metadata_repository.repository.database;

import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.repository.database.connection.TemporaryDatabaseConnection;

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
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        return null;
    }

    @Override
    String getCleanStatement(MetadataTable metadataTable, String tableNamePrefix) {
        return null;
    }

    @Override
    public String getDropStatement(MetadataTable table, String tableNamePrefix) {
        return null;
    }

}