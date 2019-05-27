package io.metadew.iesi.cockpit.backend.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.ComponentDataService;
import io.metadew.iesi.metadata.definition.Component;

public class ComponentDataServiceConfiguration extends ComponentDataService {

	private static final long serialVersionUID = 1L;

	private static ComponentDataServiceConfiguration INSTANCE;

    private List<Component> components;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ComponentDataServiceConfiguration() {
    	Component e = new Component();
    	components = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	components.add(e);
    	
    	
    }

    public synchronized static ComponentDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Component> getAllComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public synchronized void updateComponent(Component componentName) {
    	// add logic
    }

    @Override
    public synchronized Component getComponentByName(String componentName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteComponent(String componentName) {
    	// add logic
    }
}
