package io.metadew.iesi.connection.database.connection.db2;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Db2DatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<Db2DatabaseConnection> implements SchemaDatabaseConnectionService<Db2DatabaseConnection> {

    private static Db2DatabaseConnectionServiceImpl INSTANCE;

    public synchronized static Db2DatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Db2DatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private Db2DatabaseConnectionServiceImpl() {}

    @Override
    public String getDriver(Db2DatabaseConnection databaseConnection) {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    public Class<Db2DatabaseConnection> appliesTo() {
        return Db2DatabaseConnection.class;
    }

}