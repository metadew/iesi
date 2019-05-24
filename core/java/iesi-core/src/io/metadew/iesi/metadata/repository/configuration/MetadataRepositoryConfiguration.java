package io.metadew.iesi.metadata.repository.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.ControlMetadataRepository;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.ResultMetadataRepository;
import io.metadew.iesi.metadata.repository.TraceMetadataRepository;
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

	public MetadataRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
		fromConfigFile(configFile, frameworkSettingConfiguration, frameworkCrypto);
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

	private void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
		// type
		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.type").isPresent() &&
				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).isPresent()) {
			type = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get();
		} else {
			throw new RuntimeException("No type configured for the metadata repository");
		}
		// category
		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.category").isPresent() &&
				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.category").get()).isPresent()) {
			categories = Arrays.stream(configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.category").get()).get().split(","))
					.map(String::trim)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException("No category configured for the metadata repository");
		}

		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.name").isPresent() &&
				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.name").get()).isPresent()) {
			name = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.name").get()).get();
		}
		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").isPresent() &&
				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").get()).isPresent()) {
			scope = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").get()).get();
		}
		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").isPresent() &&
				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").get()).isPresent()) {
			instanceName = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").get()).get();
		}
		try {
		repositoryConfiguration = new RepositoryConfigurationFactory().createRepositoryConfiguration(configFile, frameworkSettingConfiguration, frameworkCrypto);
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
                metadataRepositories.add(new DesignMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ConnectivityMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ControlMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new TraceMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
                metadataRepositories.add(new ResultMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
                // metadataRepositories.add(new GeneralMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository()));
            } else if (category.equalsIgnoreCase("design")) {
                metadataRepositories.add(new DesignMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("connectivity")) {
                metadataRepositories.add(new ConnectivityMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("control")) {
                metadataRepositories.add(new ControlMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("trace")) {
                metadataRepositories.add(new TraceMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
            } else if (category.equalsIgnoreCase("result")) {
                metadataRepositories.add(new ResultMetadataRepository(frameworkConfiguration.getFrameworkCode(), name, scope, instanceName, repositoryConfiguration.toRepository(),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
                        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
            } else {
                throw new RuntimeException(MessageFormat.format("No Metadata repository can be created for {0}", category));
            }
        }
        return metadataRepositories;
    }
}