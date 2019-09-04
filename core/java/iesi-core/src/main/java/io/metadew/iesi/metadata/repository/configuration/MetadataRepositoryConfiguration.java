package io.metadew.iesi.metadata.repository.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata.repository.*;
import io.metadew.iesi.metadata.repository.coordinator.configuration.RepositoryConfiguration;
import io.metadew.iesi.metadata.repository.coordinator.configuration.RepositoryConfigurationFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MetadataRepositoryConfiguration {

    private String name;
    private String type;
    private List<String> categories;
    private String scope;
    private String instanceName;
    private RepositoryConfiguration repositoryConfiguration;

	public MetadataRepositoryConfiguration(ConfigFile configFile) {
		fromConfigFile(configFile);
	}

    public MetadataRepositoryConfiguration(String name, String type, List<String> categories, String scope,
                                           String instanceName, RepositoryConfiguration repositoryConfiguration) {
        this.name = name;
        this.type = type;
        this.categories = categories;
        this.scope = scope;
        this.instanceName = instanceName;
        this.repositoryConfiguration = repositoryConfiguration;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public String getType() {
        return type;
    }

    public List<String> getCategories() {
        return categories;
    }

    public Optional<String> getScope() {
        return Optional.ofNullable(scope);
    }

    public Optional<String> getInstanceName() {
        return Optional.ofNullable(instanceName);
    }

    public RepositoryConfiguration getRepositoryConfiguration() {
        return repositoryConfiguration;
    }

	private void fromConfigFile(ConfigFile configFile) {
		// type
        getSettingValue(configFile, "metadata.repository.type")
                .map(value -> type = value)
                .orElseThrow(() -> new RuntimeException("No type configured for the metadata repository"));

		// category
        getSettingValue(configFile, "metadata.repository.category").map(value -> categories = Arrays.stream(value.split(",")).map(String::trim)
                .collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("No category configured for the metadata repository"));


        getSettingValue(configFile, "metadata.repository.name")
                .map(value -> name = value);
        getSettingValue(configFile, "metadata.repository.scope")
                .map(value -> scope = value);
        getSettingValue(configFile, "metadata.repository.instance.name")
                .map(value -> instanceName = value);

		try {
		    repositoryConfiguration = new RepositoryConfigurationFactory().createRepositoryConfiguration(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public List<MetadataRepository> toMetadataRepositories(FrameworkConfiguration frameworkConfiguration) {
        // TODO: generate mist of MetadataRepositories, parse categories as list
        List<MetadataRepository> metadataRepositories = new ArrayList<>();
        for (String category : categories) {
            if (category.equalsIgnoreCase("general")) {
                // Make all repositories
                metadataRepositories.add(new CatalogMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new DesignMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ConnectivityMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ControlMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new TraceMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ResultMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
                // metadataRepositories.add(new GeneralMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository()));
            } else if (category.equalsIgnoreCase("catalog")) {
                metadataRepositories.add(new CatalogMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("design")) {
                metadataRepositories.add(new DesignMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("connectivity")) {
                metadataRepositories.add(new ConnectivityMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("control")) {
                metadataRepositories.add(new ControlMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("trace")) {
                metadataRepositories.add(new TraceMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("result")) {
                metadataRepositories.add(new ResultMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("execution_server")) {
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def")));
            } else {
                throw new RuntimeException(MessageFormat.format("No Metadata repository can be created for {0}", category));
            }
        }
        return metadataRepositories;
    }

    public Optional<String> getSettingValue(ConfigFile configFile, String settingPath) {
        return FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath).flatMap(configFile::getProperty);
    }
}