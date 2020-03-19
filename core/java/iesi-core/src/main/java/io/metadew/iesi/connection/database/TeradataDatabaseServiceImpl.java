package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class TeradataDatabaseServiceImpl extends DatabaseServiceImpl<TeradataDatabase> implements DatabaseService<TeradataDatabase>  {

    private static TeradataDatabaseServiceImpl INSTANCE;

    public synchronized static TeradataDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private TeradataDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(TeradataDatabase teradataDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(TeradataDatabase teradataDatabase, String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras(TeradataDatabase teradataDatabase) {
        return "";
    }

    @Override
    public boolean addComments(TeradataDatabase teradataDatabase) {
        return false;
    }

    @Override
    public String toQueryString(TeradataDatabase teradataDatabase, MetadataField field) {
        return "";
    }

    @Override
    public Class<TeradataDatabase> appliesTo() {
        return TeradataDatabase.class;
    }

}
