package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.grid.Grid;

import io.metadew.iesi.metadata.definition.RequestTemplate;

public class RequestTemplateGrid extends Grid<RequestTemplate> {

	private static final long serialVersionUID = 1L;

	public RequestTemplateGrid() {
        setSizeFull();

        
        
        //addComponentColumn(i -> VaadinIcon.PLAY_CIRCLE_O.create()).setHeader("Use");
        //addComponentColumn(i -> new RequestTemplateUseComponent()).setHeader("Use");
        
        addColumn(RequestTemplate::getName)
                .setHeader("Template name")
                .setFlexGrow(20)
                .setSortable(true);

        addColumn(RequestTemplate::getDescription)
                .setHeader("Description")
                .setFlexGrow(12);
               

    }
	
    public RequestTemplate getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(RequestTemplate requestTemplate) {
        getDataCommunicator().refresh(requestTemplate);
    }
}
