package io.metadew.iesi.cockpit.backend.configuration;

import java.util.Collections;
import java.util.List;

import io.metadew.iesi.cockpit.backend.EnvironmentDataService;
import io.metadew.iesi.cockpit.backend.controller.FrameworkConnection;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.EnvironmentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Environment;

public class EnvironmentDataServiceConfiguration extends EnvironmentDataService {

	private static final long serialVersionUID = 1L;

	private static EnvironmentDataServiceConfiguration INSTANCE;
	private static EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(
			FrameworkConnection.getInstance().getFrameworkInstance());


	private EnvironmentDataServiceConfiguration() {
    	
    }

    public synchronized static EnvironmentDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentDataServiceConfiguration();
        }
        return INSTANCE;
    }

    @Override
    public synchronized List<Environment> getAllEnvironments() {
    	return Collections.unmodifiableList(EnvironmentDataServiceConfiguration.getEnvironmentConfiguration().getAllEnvironments());
    }

    @Override
    public synchronized void updateEnvironment(Environment environment) {
    	try {
			EnvironmentDataServiceConfiguration.getEnvironmentConfiguration().updateEnvironment(environment);
		} catch (EnvironmentDoesNotExistException e) {
			e.printStackTrace();
		}
    }

    @Override
    public synchronized Environment getEnvironmentByName(String environmentName) {
    	return EnvironmentDataServiceConfiguration.getEnvironmentConfiguration().getEnvironment(environmentName).get();
    }

    @Override
    public synchronized void deleteEnvironment(String environmentName) {
    	EnvironmentDataServiceConfiguration.getEnvironmentConfiguration().deleteEnvironment(environmentName);
    }

	public static EnvironmentConfiguration getEnvironmentConfiguration() {
		return environmentConfiguration;
	}

	public static void setEnvironmentConfiguration(EnvironmentConfiguration environmentConfiguration) {
		EnvironmentDataServiceConfiguration.environmentConfiguration = environmentConfiguration;
	}
}
