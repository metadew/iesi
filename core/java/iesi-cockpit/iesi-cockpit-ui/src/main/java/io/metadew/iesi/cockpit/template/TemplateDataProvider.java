package io.metadew.iesi.cockpit.template;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.TemplateDataService;
import io.metadew.iesi.metadata.definition.Template;

public class TemplateDataProvider extends ListDataProvider<Template> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public TemplateDataProvider() {
        super(TemplateDataService.get().getAllRequestTemplates());
    }

    public void save(Template template) {
        boolean newRequestTemplate = template.isEmpty();

        TemplateDataService.get().updateRequestTemplate(template);
        if (newRequestTemplate) {
            refreshAll();
        } else {
            refreshItem(template);
        }
    }

    public void delete(Template template) {
    	TemplateDataService.get().deleteRequestTemplate(template.getName());
        refreshAll();
    }

    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(RequestTemplate -> passesFilter(RequestTemplate.getName(), filterText)
                || passesFilter(RequestTemplate.getDescription(), filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
