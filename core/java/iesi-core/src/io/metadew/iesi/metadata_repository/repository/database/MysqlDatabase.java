package io.metadew.iesi.metadata_repository.repository.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.repository.database.connection.MysqlDatabaseConnection;

import java.util.List;

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

    @Override
    public List<String> getAllTables(String pattern) {
        return null;
    }

}
