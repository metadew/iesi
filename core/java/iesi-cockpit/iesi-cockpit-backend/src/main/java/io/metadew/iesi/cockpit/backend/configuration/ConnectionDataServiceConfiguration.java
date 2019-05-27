package io.metadew.iesi.cockpit.backend.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.ConnectionDataService;
import io.metadew.iesi.metadata.definition.Connection;

public class ConnectionDataServiceConfiguration extends ConnectionDataService {

	private static final long serialVersionUID = 1L;

	private static ConnectionDataServiceConfiguration INSTANCE;

    private List<Connection> connections;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ConnectionDataServiceConfiguration() {
    	Connection e = new Connection();
    	connections = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	connections.add(e);
    	
    	
    }

    public synchronized static ConnectionDataServiceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Connection> getAllConnections() {
        return Collections.unmodifiableList(connections);
    }

    @Override
    public synchronized void updateConnection(Connection connectionName) {
    	// add logic
    }

    @Override
    public synchronized Connection getConnectionByName(String connectionName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteConnection(String connectionName) {
    	// add logic
    }
}
