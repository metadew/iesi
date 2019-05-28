package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.RequestTemplateDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.RequestTemplate;

public abstract class RequestTemplateDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<RequestTemplate> getAllRequestTemplates();

    public abstract void updateRequestTemplate(RequestTemplate requestTemplate);

    public abstract void deleteRequestTemplate(String requestTemplateName);

	public abstract RequestTemplate getRequestTemplateByName(String requestTemplateName);

    public static RequestTemplateDataService get() {
        return RequestTemplateDataServiceConfiguration.getInstance();
    }

}
