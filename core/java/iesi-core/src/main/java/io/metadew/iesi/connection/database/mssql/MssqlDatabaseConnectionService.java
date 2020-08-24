package io.metadew.iesi.connection.database.mssql;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MssqlDatabaseConnectionService extends SchemaDatabaseConnectionService<MssqlDatabaseConnection> implements ISchemaDatabaseConnectionService<MssqlDatabaseConnection> {

    private static MssqlDatabaseConnectionService INSTANCE;

    public synchronized static MssqlDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private MssqlDatabaseConnectionService() {}
    
    @Override
    public String getDriver(MssqlDatabaseConnection databaseConnection) {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public Class<MssqlDatabaseConnection> appliesTo() {
        return MssqlDatabaseConnection.class;
    }

}