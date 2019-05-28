package io.metadew.iesi.cockpit.script;

import com.vaadin.flow.component.grid.Grid;
import io.metadew.iesi.metadata.definition.Script;

public class ScriptGrid extends Grid<Script> {

	private static final long serialVersionUID = 1L;

	public ScriptGrid() {
        setSizeFull();

        addColumn(Script::getName)
                .setHeader("Script name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(Script::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
    }

    public Script getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Script script) {
        getDataCommunicator().refresh(script);
    }
}
