package io.metadew.iesi.cockpit.define.environment;

import com.vaadin.flow.component.grid.Grid;
import io.metadew.iesi.metadata.definition.Environment;

public class EnvironmentGrid extends Grid<Environment> {

	private static final long serialVersionUID = 1L;

	public EnvironmentGrid() {
        setSizeFull();

        addColumn(Environment::getName)
                .setHeader("Environment name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(Environment::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
    }

    public Environment getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Environment environment) {
        getDataCommunicator().refresh(environment);
    }
}
