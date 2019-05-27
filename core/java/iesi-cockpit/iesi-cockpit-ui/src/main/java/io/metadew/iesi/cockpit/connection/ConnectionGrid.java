package io.metadew.iesi.cockpit.connection;

import com.vaadin.flow.component.grid.Grid;
import io.metadew.iesi.metadata.definition.Connection;

public class ConnectionGrid extends Grid<Connection> {

	private static final long serialVersionUID = 1L;

	public ConnectionGrid() {
        setSizeFull();

        addColumn(Connection::getName)
                .setHeader("Connection name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(Connection::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
    }

    public Connection getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Connection connection) {
        getDataCommunicator().refresh(connection);
    }
}
