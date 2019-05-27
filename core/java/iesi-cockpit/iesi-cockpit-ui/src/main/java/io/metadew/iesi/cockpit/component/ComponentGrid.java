package io.metadew.iesi.cockpit.component;

import com.vaadin.flow.component.grid.Grid;
import io.metadew.iesi.metadata.definition.Component;

public class ComponentGrid extends Grid<Component> {

	private static final long serialVersionUID = 1L;

	public ComponentGrid() {
        setSizeFull();

        addColumn(Component::getName)
                .setHeader("Component name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(Component::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
    }

    public Component getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Component component) {
        getDataCommunicator().refresh(component);
    }
}
