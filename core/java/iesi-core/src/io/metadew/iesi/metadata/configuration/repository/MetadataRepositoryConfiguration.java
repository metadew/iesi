//package io.metadew.iesi.metadata.configuration.repository;
//
//import io.metadew.iesi.common.config.ConfigFile;
//import io.metadew.iesi.common.config.repository.RepositoryConfigurationFactory;
//import io.metadew.iesi.common.config.repository.RepositoryDatabaseConfiguration;
//import io.metadew.iesi.connection.DatabaseConnection;
//import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
//import io.metadew.iesi.metadata_repository.MetadataRepository;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//public class MetadataRepositoryConfiguration {
//
//	private String name;
//	private String type;
//	private String category;
//	private String scope;
//	private String instanceName;
//	private RepositoryDatabaseConfiguration repositoryDatabaseConfiguration;
//
//	public MetadataRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
//		fromConfigFile(configFile, frameworkSettingConfiguration);
//	}
//
//	public MetadataRepositoryConfiguration(String name, String type, String category, String scope,
//										   String instanceName, RepositoryDatabaseConfiguration repositoryDatabaseConfiguration) {
//		this.name = name;
//		this.type = type;
//		this.category = category;
//		this.scope = scope;
//		this.instanceName = instanceName;
//		this.repositoryDatabaseConfiguration = repositoryDatabaseConfiguration;
//	}
//
//	public Optional<String> getName() {
//		return Optional.ofNullable(name);
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public String getCategory() {
//		return category;
//	}
//
//	public Optional<String> getScope() {
//		return Optional.ofNullable(scope);
//	}
//
//	public Optional<String> getInstanceName() {
//		return Optional.ofNullable(instanceName);
//	}
//
//	public RepositoryDatabaseConfiguration getRepositoryConnection() {
//		return repositoryDatabaseConfiguration;
//	}
//
//	private void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
//		// type
//		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.type").isPresent() &&
//				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).isPresent()) {
//			type = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get();
//		} else {
//			throw new RuntimeException("No type configured for the metadata repository");
//		}
//		// category
//		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.category").isPresent() &&
//				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.category").get()).isPresent()) {
//			category = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.category").get()).get();
//		} else {
//			throw new RuntimeException("No type configured for the metadata repository");
//		}
//
//		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.name").isPresent() &&
//				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.name").get()).isPresent()) {
//			name = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.name").get()).get();
//		}
//		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").isPresent() &&
//				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").get()).isPresent()) {
//			scope = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.scope").get()).get();
//		}
//		if (frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").isPresent() &&
//				configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").get()).isPresent()) {
//			instanceName = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.instance.name").get()).get();
//		}
//		repositoryDatabaseConfiguration = new RepositoryConfigurationFactory().createRepositoryConfiguration(configFile, frameworkSettingConfiguration);
//	}
//
//	public io.metadew.iesi.metadata_repository.MetadataRepository toMetadataRepository() {
//		Map<String, DatabaseConnection> databaseConnections = new HashMap<>();
//		databaseConnections.put("owner", repositoryDatabaseConfiguration.toConnection("owner"));
//		databaseConnections.put("writer", repositoryDatabaseConfiguration.toConnection("writer"));
//		databaseConnections.put("reader", repositoryDatabaseConfiguration.toConnection("reader"));
//		return new MetadataRepository(category, databaseConnections);
//	}
//}