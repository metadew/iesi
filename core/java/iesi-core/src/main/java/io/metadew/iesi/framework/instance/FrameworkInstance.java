package io.metadew.iesi.framework.instance;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.Framework;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.runtime.Executor;
import io.metadew.iesi.runtime.Requestor;
import io.metadew.iesi.server.execution.tools.ExecutionServerTools;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FrameworkInstance {

	private FrameworkConfiguration frameworkConfiguration;
	private FrameworkCrypto frameworkCrypto;
	private FrameworkControl frameworkControl;
	private MetadataControl metadataControl;
	private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;
	private String executionServerFilePath;
	private FrameworkInitializationFile frameworkInitializationFile;


	private static FrameworkInstance INSTANCE;

	public synchronized static FrameworkInstance getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkInstance();
		}
		return INSTANCE;
	}

	private FrameworkInstance() {}


	public void init() {
		init("write", null);
	}

	public void init(FrameworkInitializationFile frameworkInitializationFile) {
		init("write", frameworkInitializationFile);
	}

	public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile) {
		// Get the framework configuration
		this.frameworkConfiguration = FrameworkConfiguration.getInstance();
		frameworkConfiguration.init();

		this.frameworkCrypto = FrameworkCrypto.getInstance();

		// Set appropriate initialization file
		if (frameworkInitializationFile == null || frameworkInitializationFile.getName().trim().isEmpty()) {
			this.frameworkInitializationFile = new FrameworkInitializationFile(this.frameworkConfiguration.getFrameworkCode() + "-conf.ini");
		} else {
			this.frameworkInitializationFile = frameworkInitializationFile;
		}

		// Prepare configuration and shared Metadata
		this.frameworkControl = FrameworkControl.getInstance();
		frameworkControl.init(frameworkConfiguration, logonType, this.frameworkInitializationFile, frameworkCrypto);

		frameworkConfiguration.setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());

		this.metadataControl = MetadataControl.getInstance();
		metadataControl.init(frameworkControl.getMetadataRepositoryConfigurations()
				.stream().map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration))
				.collect(ArrayList::new, List::addAll, List::addAll));

		// Set up connection to the metadata repository
		this.executionServerFilePath = frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("run.exec")
						+ File.separator + "ExecutionServerRepository.db3";

		if (!ExecutionServerTools.getServerMode(this).equalsIgnoreCase("off")) {
			if (!FileTools.exists(this.getExecutionServerFilePath())) {
				throw new RuntimeException("framework.server.repository.notfound");
			}
		}

		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(
				this.getExecutionServerFilePath());
		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
		Map<String, Database> databases = new HashMap<>();
		databases.put("reader", sqliteDatabase);
		databases.put("writer", sqliteDatabase);
		databases.put("owner", sqliteDatabase);
		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
		this.executionServerRepositoryConfiguration = new ExecutionServerMetadataRepository(
				frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"));
		Executor.getInstance().init(this);
	}

	public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile,
			FrameworkConfiguration frameworkConfiguration) {
		this.frameworkConfiguration = frameworkConfiguration;
		this.frameworkInitializationFile = frameworkInitializationFile;
		this.frameworkCrypto = FrameworkCrypto.getInstance();

		this.frameworkControl = FrameworkControl.getInstance();
		frameworkControl.init(this.frameworkConfiguration, logonType, this.frameworkInitializationFile, frameworkCrypto);

		this.frameworkConfiguration.setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());

		this.metadataControl = MetadataControl.getInstance();
		metadataControl.init(this.frameworkControl.getMetadataRepositoryConfigurations().stream()
				.map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration))
				.flatMap(Collection::stream).collect(Collectors.toList()));

		// Set up connection to the metadata repository
		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(
				this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.exec")
						+ File.separator + "ExecutionServerRepository.db3");
		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
		Map<String, Database> databases = new HashMap<>();
		databases.put("reader", sqliteDatabase);
		databases.put("writer", sqliteDatabase);
		databases.put("owner", sqliteDatabase);
		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
		this.executionServerRepositoryConfiguration = (new ExecutionServerMetadataRepository(
				frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def"),
				frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("metadata.def")));

		Executor.getInstance().init(this);
		Requestor.getInstance().init(this);
	}

	// Getters and Setters
	public ExecutionServerMetadataRepository getExecutionServerRepositoryConfiguration() {
		return executionServerRepositoryConfiguration;
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

	public FrameworkInitializationFile getFrameworkInitializationFile() {
		return frameworkInitializationFile;
	}

	public String getExecutionServerFilePath() {
		return executionServerFilePath;
	}
}