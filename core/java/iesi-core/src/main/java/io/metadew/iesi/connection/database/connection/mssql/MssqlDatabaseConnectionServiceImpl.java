package io.metadew.iesi.connection.database.connection.mssql;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MssqlDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<MssqlDatabaseConnection> implements SchemaDatabaseConnectionService<MssqlDatabaseConnection> {

    private static MssqlDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static MssqlDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private MssqlDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(MssqlDatabaseConnection databaseConnection) {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public Class<MssqlDatabaseConnection> appliesTo() {
        return MssqlDatabaseConnection.class;
    }
}