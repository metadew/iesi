package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.ConnectionDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.Connection;

public abstract class ConnectionDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<Connection> getAllConnections();

    public abstract void updateConnection(Connection connection);

    public abstract void deleteConnection(String connectionName);

	public abstract Connection getConnectionByName(String connectionName);

    public static ConnectionDataService get() {
        return ConnectionDataServiceConfiguration.getInstance();
    }

}
