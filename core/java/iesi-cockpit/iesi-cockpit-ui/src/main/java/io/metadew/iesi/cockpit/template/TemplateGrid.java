package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.grid.Grid;

import io.metadew.iesi.metadata.definition.Template;

public class TemplateGrid extends Grid<Template> {

	private static final long serialVersionUID = 1L;

	public TemplateGrid() {
        setSizeFull();

        
        
        //addComponentColumn(i -> VaadinIcon.PLAY_CIRCLE_O.create()).setHeader("Use");
        //addComponentColumn(i -> new TemplateUseComponent()).setHeader("Use");
        
        addColumn(Template::getName)
                .setHeader("Template name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(Template::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
               

    }
	
    public Template getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Template template) {
        getDataCommunicator().refresh(template);
    }
}
