package io.metadew.iesi.cockpit.define.environment;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.EnvironmentDataService;
import io.metadew.iesi.metadata.definition.Environment;

public class EnvironmentDataProvider extends ListDataProvider<Environment> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public EnvironmentDataProvider() {
        super(EnvironmentDataService.get().getAllEnvironments());
    }

    public void save(Environment environment) {
        boolean newEnvironment = environment.isEmpty();

        EnvironmentDataService.get().updateEnvironment(environment);
        if (newEnvironment) {
            refreshAll();
        } else {
            refreshItem(environment);
        }
    }

    public void delete(Environment environment) {
    	EnvironmentDataService.get().deleteEnvironment(environment.getName());
        refreshAll();
    }

    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(Environment -> passesFilter(Environment.getName(), filterText)
                || passesFilter(Environment.getDescription(), filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
