package io.metadew.iesi.cockpit.connection;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import io.metadew.iesi.metadata.definition.Connection;

public class ConnectionForm extends Div {

	private static final long serialVersionUID = 1L;

	private VerticalLayout content;

    private TextField connectionName;
    private TextField connectionDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private ConnectionLogic viewLogic;
    private Binder<Connection> binder;
    private Connection currentConnection;

    public ConnectionForm(ConnectionLogic ConnectionLogic) {
        setClassName("Connection-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = ConnectionLogic;

        connectionName = new TextField("Name");
        connectionName.setWidth("100%");
        connectionName.setRequired(true);
        connectionName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(connectionName);

        connectionDescription = new TextField("Description");
        connectionDescription.setWidth("100%");
        connectionDescription.setRequired(false);
        connectionDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(connectionDescription);

        binder = new BeanValidationBinder<>(Connection.class);
        binder.forField(connectionName).bind("name");
        binder.forField(connectionDescription).bind("description");
        binder.bindInstanceFields(this);

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
        });

        save = new Button("Save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentConnection != null
                    && binder.writeBeanIfValid(currentConnection)) {
                viewLogic.saveConnection(currentConnection);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> viewLogic.editConnection(currentConnection));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelConnection());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelConnection())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentConnection != null) {
                viewLogic.deleteConnection(currentConnection);
            }
        });

        content.add(save, discard, delete, cancel);
    }

    public void editConnection(Connection connection) {
        if (connection == null) {
        	connection = new Connection();
        }
        delete.setVisible(!connection.isEmpty());
        currentConnection = connection;
        binder.readBean(connection);
    }

}
