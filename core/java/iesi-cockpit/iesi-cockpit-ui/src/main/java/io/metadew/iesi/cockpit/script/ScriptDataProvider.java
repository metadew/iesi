package io.metadew.iesi.cockpit.script;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.ScriptDataService;
import io.metadew.iesi.metadata.definition.Script;

public class ScriptDataProvider extends ListDataProvider<Script> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public ScriptDataProvider() {
        super(ScriptDataService.get().getAllScripts());
    }

    public void save(Script script) {
        boolean newScript = script.isEmpty();

        ScriptDataService.get().updateScript(script);
        if (newScript) {
            refreshAll();
        } else {
            refreshItem(script);
        }
    }

    public void delete(Script script) {
    	ScriptDataService.get().deleteScript(script.getName());
        refreshAll();
    }

    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(Script -> passesFilter(Script.getName(), filterText)
                || passesFilter(Script.getDescription(), filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
