package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MysqlDatabaseServiceImpl extends DatabaseServiceImpl<MysqlDatabase> implements DatabaseService<MysqlDatabase>  {

    private static MysqlDatabaseServiceImpl INSTANCE;

    private static final String keyword= "db.mysql";


    public synchronized static MysqlDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MysqlDatabaseServiceImpl() {}

    @Override
    public MysqlDatabase getDatabase(Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return keyword;
    }

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
