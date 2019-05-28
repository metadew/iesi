package io.metadew.iesi.cockpit.connection;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import io.metadew.iesi.cockpit.MainLayout;
import io.metadew.iesi.metadata.definition.Connection;

@Route(value = "connections", layout = MainLayout.class)
public class ConnectionView extends HorizontalLayout
        implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "connections";
    private ConnectionGrid grid;
    private ConnectionForm form;
    private TextField filter;

    private ConnectionLogic viewLogic = new ConnectionLogic(this);
    private Button newConnection;

    private ConnectionDataProvider dataProvider = new ConnectionDataProvider();

    public ConnectionView() {
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new ConnectionGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new ConnectionForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter name or description");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newConnection = new Button("New Connection");
        newConnection.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newConnection.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newConnection.addClickListener(click -> viewLogic.newConnection());
        // CTRL+N will create a new window which is unavoidable
        newConnection.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newConnection);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    public void showError(String msg) {
        Notification.show(msg);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    public void setNewConnectionEnabled(boolean enabled) {
        newConnection.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void selectRow(Connection row) {
        grid.getSelectionModel().select(row);
    }

    public Connection getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void updateConnection(Connection Connection) {
        dataProvider.save(Connection);
    }

    public void removeConnection(Connection Connection) {
        dataProvider.delete(Connection);
    }

    public void editConnection(Connection connection) {
        showForm(connection != null);
        form.editConnection(connection);
    }

    public void showForm(boolean show) {
        form.setVisible(show);

        /* FIXME The following line should be uncommented when the CheckboxGroup
         * issue is resolved. The category CheckboxGroup throws an
         * IllegalArgumentException when the form is disabled.
         */
        //form.setEnabled(show);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }
}
