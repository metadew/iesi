package io.metadew.iesi.connection.database.connection.presto;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PrestoDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<PrestoDatabaseConnection> implements SchemaDatabaseConnectionService<PrestoDatabaseConnection> {

    private static PrestoDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static PrestoDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private PrestoDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(PrestoDatabaseConnection databaseConnection) {
        return "io.prestosql.jdbc.PrestoDriver";
    }

    @Override
    public Class<PrestoDatabaseConnection> appliesTo() {
        return PrestoDatabaseConnection.class;
    }
}