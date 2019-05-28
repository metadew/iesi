package io.metadew.iesi.cockpit.template;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.ListDataProvider;

import io.metadew.iesi.cockpit.backend.RequestTemplateDataService;
import io.metadew.iesi.metadata.definition.RequestTemplate;

public class RequestTemplateDataProvider extends ListDataProvider<RequestTemplate> {

	private static final long serialVersionUID = 1L;
	/** Text filter that can be changed separately. */
    private String filterText = "";

    public RequestTemplateDataProvider() {
        super(RequestTemplateDataService.get().getAllRequestTemplates());
    }

    public void save(RequestTemplate requestTemplate) {
        boolean newRequestTemplate = requestTemplate.isEmpty();

        RequestTemplateDataService.get().updateRequestTemplate(requestTemplate);
        if (newRequestTemplate) {
            refreshAll();
        } else {
            refreshItem(requestTemplate);
        }
    }

    public void delete(RequestTemplate requestTemplate) {
    	RequestTemplateDataService.get().deleteRequestTemplate(requestTemplate.getName());
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
