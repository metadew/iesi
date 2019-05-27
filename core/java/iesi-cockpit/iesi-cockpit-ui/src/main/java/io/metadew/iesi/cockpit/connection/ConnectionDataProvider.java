package io.metadew.iesi.cockpit.connection;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.ConnectionDataService;
import io.metadew.iesi.metadata.definition.Connection;

public class ConnectionDataProvider extends ListDataProvider<Connection> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public ConnectionDataProvider() {
        super(ConnectionDataService.get().getAllConnections());
    }

    public void save(Connection connection) {
        boolean newConnection = connection.isEmpty();

        ConnectionDataService.get().updateConnection(connection);
        if (newConnection) {
            refreshAll();
        } else {
            refreshItem(connection);
        }
    }

    public void delete(Connection connection) {
    	ConnectionDataService.get().deleteConnection(connection.getName());
        refreshAll();
    }

    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(Connection -> passesFilter(Connection.getName(), filterText)
                || passesFilter(Connection.getDescription(), filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
