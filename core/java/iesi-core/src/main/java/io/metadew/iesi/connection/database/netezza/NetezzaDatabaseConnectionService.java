package io.metadew.iesi.connection.database.netezza;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NetezzaDatabaseConnectionService extends SchemaDatabaseConnectionService<NetezzaDatabaseConnection> implements ISchemaDatabaseConnectionService<NetezzaDatabaseConnection> {

    private static NetezzaDatabaseConnectionService INSTANCE;

    public synchronized static NetezzaDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private NetezzaDatabaseConnectionService() {}
    
    @Override
    public String getDriver(NetezzaDatabaseConnection databaseConnection) {
        return "org.netezza.Driver";
    }

    @Override
    public Class<NetezzaDatabaseConnection> appliesTo() {
        return NetezzaDatabaseConnection.class;
    }
}