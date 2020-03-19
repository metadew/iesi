package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

public class MariadbDatabaseServiceImpl extends DatabaseServiceImpl<MariadbDatabase> implements DatabaseService<MariadbDatabase>  {

    private static MariadbDatabaseServiceImpl INSTANCE;

    public synchronized static MariadbDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MariadbDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MariadbDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(MariadbDatabase mariadbDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MariadbDatabase mariadbDatabase, String pattern) {
        return null;
    }

    @Override
    public String getCreateStatement(MariadbDatabase mariadbDatabase, MetadataTable table, String tableNamePrefix) {
        return null;
    }

    @Override
    public String createQueryExtras(MariadbDatabase mariadbDatabase) {
        return null;
    }

    @Override
    public boolean addComments(MariadbDatabase mariadbDatabase) {
        return false;
    }

    @Override
    public String toQueryString(MariadbDatabase mariadbDatabase, MetadataField field) {
        return null;
    }

    @Override
    public Class<MariadbDatabase> appliesTo() {
        return MariadbDatabase.class;
    }


}
