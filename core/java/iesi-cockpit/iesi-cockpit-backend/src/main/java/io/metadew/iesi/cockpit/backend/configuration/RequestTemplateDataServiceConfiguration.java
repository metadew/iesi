package io.metadew.iesi.cockpit.backend.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.RequestTemplateDataService;
import io.metadew.iesi.metadata.definition.RequestTemplate;

public class RequestTemplateDataServiceConfiguration extends RequestTemplateDataService {

	private static final long serialVersionUID = 1L;

	private static RequestTemplateDataServiceConfiguration INSTANCE;

    private List<RequestTemplate> requestTemplates;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private RequestTemplateDataServiceConfiguration() {
    	RequestTemplate e = new RequestTemplate();
    	requestTemplates = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	requestTemplates.add(e);
    	
    	
    }

    public synchronized static RequestTemplateDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestTemplateDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<RequestTemplate> getAllRequestTemplates() {
        return Collections.unmodifiableList(requestTemplates);
    }

    @Override
    public synchronized void updateRequestTemplate(RequestTemplate requestTemplateName) {
    	// add logic
    }

    @Override
    public synchronized RequestTemplate getRequestTemplateByName(String requestTemplateName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteRequestTemplate(String requestTemplateName) {
    	// add logic
    }
}
