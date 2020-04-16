package io.metadew.iesi.connection.database.connection.netezza;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NetezzaDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<NetezzaDatabaseConnection> implements SchemaDatabaseConnectionService<NetezzaDatabaseConnection> {

    private static NetezzaDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static NetezzaDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private NetezzaDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(NetezzaDatabaseConnection databaseConnection) {
        return "org.netezza.Driver";
    }

    @Override
    public Class<NetezzaDatabaseConnection> appliesTo() {
        return NetezzaDatabaseConnection.class;
    }
}