package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.configuration.EnvironmentDataServiceConfiguration;
import io.metadew.iesi.metadata.definition.Environment;

public abstract class EnvironmentDataService implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Collection<Environment> getAllEnvironments();

    public abstract void updateEnvironment(Environment environment);

    public abstract void deleteEnvironment(String environmentName);

	public abstract Environment getEnvironmentByName(String environmentName);

    public static EnvironmentDataService get() {
        return EnvironmentDataServiceConfiguration.getInstance();
    }

}
