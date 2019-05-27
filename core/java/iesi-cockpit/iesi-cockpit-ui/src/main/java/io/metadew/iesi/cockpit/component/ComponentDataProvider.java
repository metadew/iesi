package io.metadew.iesi.cockpit.component;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.ComponentDataService;
import io.metadew.iesi.metadata.definition.Component;

public class ComponentDataProvider extends ListDataProvider<Component> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public ComponentDataProvider() {
        super(ComponentDataService.get().getAllComponents());
    }

    public void save(Component component) {
        boolean newComponent = component.isEmpty();

        ComponentDataService.get().updateComponent(component);
        if (newComponent) {
            refreshAll();
        } else {
            refreshItem(component);
        }
    }

    public void delete(Component component) {
    	ComponentDataService.get().deleteComponent(component.getName());
        refreshAll();
    }

    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(Component -> passesFilter(Component.getName(), filterText)
                || passesFilter(Component.getDescription(), filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
