package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.ComponentDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.Component;

public abstract class ComponentDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<Component> getAllComponents();

    public abstract void updateComponent(Component component);

    public abstract void deleteComponent(String componentName);

	public abstract Component getComponentByName(String componentName);

    public static ComponentDataService get() {
        return ComponentDataServiceConfiguration.getInstance();
    }

}
