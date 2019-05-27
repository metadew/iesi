package io.metadew.iesi.cockpit.backend.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.EnvironmentDataService;
import io.metadew.iesi.metadata.definition.Environment;

public class EnvironmentDataServiceConfiguration extends EnvironmentDataService {

	private static final long serialVersionUID = 1L;

	private static EnvironmentDataServiceConfiguration INSTANCE;

    private List<Environment> environments;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private EnvironmentDataServiceConfiguration() {
    	Environment e = new Environment();
    	environments = new ArrayList();
    	e.setName("test");
    	e.setDescription("ok");
    	environments.add(e);
    	
    	
    }

    public synchronized static EnvironmentDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Environment> getAllEnvironments() {
        return Collections.unmodifiableList(environments);
    }

    @Override
    public synchronized void updateEnvironment(Environment environmentName) {
    	// add logic
    }

    @Override
    public synchronized Environment getEnvironmentByName(String environmentName) {
    	// add logic
    	return null;
    }

    @Override
    public synchronized void deleteEnvironment(String environmentName) {
    	// add logic
    }
}
