package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.TemplateDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.Template;

public abstract class TemplateDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<Template> getAllRequestTemplates();

    public abstract void updateRequestTemplate(Template template);

    public abstract void deleteRequestTemplate(String requestTemplateName);

	public abstract Template getRequestTemplateByName(String requestTemplateName);

    public static TemplateDataService get() {
        return TemplateDataServiceConfiguration.getInstance();
    }

}
