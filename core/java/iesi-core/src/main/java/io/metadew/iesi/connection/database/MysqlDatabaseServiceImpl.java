package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class MysqlDatabaseServiceImpl extends DatabaseServiceImpl<MysqlDatabase> implements DatabaseService<MysqlDatabase>  {

    private static MysqlDatabaseServiceImpl INSTANCE;

    public synchronized static MysqlDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MysqlDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(MysqlDatabase mysqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MysqlDatabase mysqlDatabase, String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras(MysqlDatabase mysqlDatabase) {
        return null;
    }

    @Override
    public boolean addComments(MysqlDatabase mysqlDatabase) {
        return false;
    }

    @Override
    public String toQueryString(MysqlDatabase mysqlDatabase, MetadataField field) {
        return null;
    }

    @Override
    public Class<MysqlDatabase> appliesTo() {
        return MysqlDatabase.class;
    }


}
