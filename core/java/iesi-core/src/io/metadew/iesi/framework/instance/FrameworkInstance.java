package io.metadew.iesi.framework.instance;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FrameworkInstance {

	private FrameworkConfiguration frameworkConfiguration;
	private FrameworkCrypto frameworkCrypto;
	private FrameworkControl frameworkControl;
	private MetadataControl metadataControl;
	private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;
	private FrameworkInitializationFile frameworkInitializationFile;


	// Constructors
	public FrameworkInstance() {
		this.initializeFrameworkExecution("write", null);
	}

	public FrameworkInstance(FrameworkInitializationFile frameworkInitializationFile) {
		this.initializeFrameworkExecution("write", frameworkInitializationFile);
	}

	public FrameworkInstance(String logonType, FrameworkInitializationFile frameworkInitializationFile) {
		this.initializeFrameworkExecution(logonType, frameworkInitializationFile);
	}

	public FrameworkInstance(String logonType, FrameworkInitializationFile frameworkInitializationFile, FrameworkConfiguration frameworkConfiguration) {
		this.frameworkConfiguration = frameworkConfiguration;
		this.frameworkInitializationFile = frameworkInitializationFile;
		this.frameworkCrypto = new FrameworkCrypto();
		this.frameworkControl = new FrameworkControl(this.frameworkConfiguration, logonType, this.frameworkInitializationFile, frameworkCrypto);
		this.frameworkConfiguration.setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());
		this.metadataControl = new MetadataControl(this.frameworkControl.getMetadataRepositoryConfigurations().stream()
				.map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));
		// Set up connection to the metadata repository
		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(
				this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.exec") + File.separator + "ExecutionServerRepository.db3");
		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
		Map<String, Database> databases = new HashMap<>();
		databases.put("reader", sqliteDatabase);
		databases.put("writer", sqliteDatabase);
		databases.put("owner", sqliteDatabase);
		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
		this.executionServerRepositoryConfiguration = (new ExecutionServerMetadataRepository(frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
	}

	// Methods
	private void initializeFrameworkExecution(String logonType, FrameworkInitializationFile frameworkInitializationFile) {
		// Get the framework configuration
		this.setFrameworkConfiguration(new FrameworkConfiguration());
		this.setFrameworkCrypto(new FrameworkCrypto());

		// Set appropriate initialization file
		if (frameworkInitializationFile == null) {
			this.setFrameworkInitializationFile(new FrameworkInitializationFile());
			this.getFrameworkInitializationFile().setName(this.getFrameworkConfiguration().getFrameworkCode() + "-conf.ini");
		} else if (frameworkInitializationFile.getName().trim().isEmpty()) {
			this.setFrameworkInitializationFile(new FrameworkInitializationFile());
			this.getFrameworkInitializationFile().setName(this.getFrameworkConfiguration().getFrameworkCode() + "-conf.ini");			
		} else {
			this.setFrameworkInitializationFile(frameworkInitializationFile);
		}
				
		// Prepare configuration and shared Metadata
		this.setFrameworkControl(new FrameworkControl(this.getFrameworkConfiguration(), logonType, this.getFrameworkInitializationFile(), this.getFrameworkCrypto()));
		this.getFrameworkConfiguration().setActionTypesFromPlugins(this.getFrameworkControl().getFrameworkPluginConfigurationList());
		this.setMetadataControl(new MetadataControl(this.getFrameworkControl().getMetadataRepositoryConfigurations().stream().map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration)).collect(ArrayList::new, List::addAll, List::addAll)));

		// Set up connection to the metadata repository
		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(
				this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.exec") + File.separator + "ExecutionServerRepository.db3");
		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
		Map<String, Database> databases = new HashMap<>();
		databases.put("reader", sqliteDatabase);
		databases.put("writer", sqliteDatabase);
		databases.put("owner", sqliteDatabase);
		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
		this.setExecutionServerRepositoryConfiguration(new ExecutionServerMetadataRepository(frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));
	}

	public void setSettingsList(String input) {
		this.getFrameworkControl().setSettingsList(input);
	}

	// Getters and Setters
	public ExecutionServerMetadataRepository getExecutionServerRepositoryConfiguration() {
		return executionServerRepositoryConfiguration;
	}

	public void setExecutionServerRepositoryConfiguration(
			ExecutionServerMetadataRepository executionServerRepositoryConfiguration) {
		this.executionServerRepositoryConfiguration = executionServerRepositoryConfiguration;
	}

	public FrameworkConfiguration getFrameworkConfiguration() {
		return frameworkConfiguration;
	}

	public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
		this.frameworkConfiguration = frameworkConfiguration;
	}

	public FrameworkCrypto getFrameworkCrypto() {
		return frameworkCrypto;
	}

	public void setFrameworkCrypto(FrameworkCrypto frameworkCrypto) {
		this.frameworkCrypto = frameworkCrypto;
	}

	public MetadataControl getMetadataControl() {
		return metadataControl;
	}

	public void setMetadataControl(MetadataControl metadataControl) {
		this.metadataControl = metadataControl;
	}

	public FrameworkControl getFrameworkControl() {
		return frameworkControl;
	}

	public void setFrameworkControl(FrameworkControl frameworkControl) {
		this.frameworkControl = frameworkControl;
	}

	public FrameworkInitializationFile getFrameworkInitializationFile() {
		return frameworkInitializationFile;
	}

	public void setFrameworkInitializationFile(FrameworkInitializationFile frameworkInitializationFile) {
		this.frameworkInitializationFile = frameworkInitializationFile;
	}
}