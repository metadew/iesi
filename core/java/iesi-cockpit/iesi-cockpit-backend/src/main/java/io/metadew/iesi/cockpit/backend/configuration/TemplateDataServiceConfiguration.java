package io.metadew.iesi.cockpit.backend.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.TemplateDataService;
import io.metadew.iesi.metadata.definition.Template;

public class TemplateDataServiceConfiguration extends TemplateDataService {

	private static final long serialVersionUID = 1L;

	private static TemplateDataServiceConfiguration INSTANCE;

    private List<Template> templates;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private TemplateDataServiceConfiguration() {
    	Template e = new Template();
    	templates = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	templates.add(e);
    	
    	
    }

    public synchronized static TemplateDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Template> getAllRequestTemplates() {
        return Collections.unmodifiableList(templates);
    }

    @Override
    public synchronized void updateRequestTemplate(Template requestTemplateName) {
    	// add logic
    }

    @Override
    public synchronized Template getRequestTemplateByName(String requestTemplateName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteRequestTemplate(String requestTemplateName) {
    	// add logic
    }
}
