package io.metadew.iesi.cockpit.connection;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.ConnectionDataService;
import io.metadew.iesi.metadata.definition.Connection;

import java.io.Serializable;

public class ConnectionLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private ConnectionView view;

    public ConnectionLogic(ConnectionView connectionView) {
        view = connectionView;
    }

    public void init() {
        editConnection(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewConnectionEnabled(false);
        }
    }

    public void cancelConnection() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String connectionName) {
        String fragmentParameter;
        if (connectionName == null || connectionName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = connectionName;
        }

        UI.getCurrent().navigate(ConnectionView.class, fragmentParameter);
    }

    public void enter(String connectionName) {
        if (connectionName != null && !connectionName.isEmpty()) {
            if (connectionName.equals("new")) {
                newConnection();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    Connection connection = findConnection(connectionName);
                    view.selectRow(connection);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Connection findConnection(String connectionName) {
        return ConnectionDataService.get().getConnectionByName(connectionName);
    }

    public void saveConnection(Connection connection) {
        boolean newConnection = connection.isEmpty();
        view.clearSelection();
        view.updateConnection(connection);
        setFragmentParameter("");
        view.showSaveNotification(connection.getName()
                + (newConnection ? " created" : " updated"));
    }

    public void deleteConnection(Connection connection) {
        view.clearSelection();
        view.removeConnection(connection);
        setFragmentParameter("");
        view.showSaveNotification(connection.getName() + " deleted");
    }

    public void editConnection(Connection connection) {
        if (connection == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(connection.getName() + "");
        }
        view.editConnection(connection);
    }

    public void newConnection() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editConnection(new Connection());
    }

    public void rowSelected(Connection connection) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editConnection(connection);
        }
    }
}
