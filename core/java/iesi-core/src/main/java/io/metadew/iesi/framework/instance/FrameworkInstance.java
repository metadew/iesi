package io.metadew.iesi.framework.instance;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkActionTypeConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.runtime.Executor;
import io.metadew.iesi.runtime.ExecutorService;
import io.metadew.iesi.runtime.Requestor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkInstance {

	private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;
	private String executionServerFilePath;

	private static FrameworkInstance INSTANCE;

	public synchronized static FrameworkInstance getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkInstance();
		}
		return INSTANCE;
	}

	private FrameworkInstance() {}


	public void init() {
		init(new FrameworkInitializationFile(), new FrameworkExecutionContext(new Context("general", "")));
	}

	public void init(FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext context) {
		init("write", frameworkInitializationFile, context);
	}

	public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext context) {
		// Get the framework configuration
		FrameworkConfiguration frameworkConfiguration = FrameworkConfiguration.getInstance();
		frameworkConfiguration.init();

		FrameworkCrypto frameworkCrypto = FrameworkCrypto.getInstance();

		// Set appropriate initialization file
		if (frameworkInitializationFile.getName().trim().isEmpty()) {
			frameworkInitializationFile = new FrameworkInitializationFile(frameworkConfiguration.getFrameworkCode() + "-conf.ini");
		}

		// Prepare configuration and shared Metadata
		FrameworkControl frameworkControl = FrameworkControl.getInstance();
		frameworkControl.init(frameworkConfiguration, logonType, frameworkInitializationFile, frameworkCrypto);

		FrameworkActionTypeConfiguration.getInstance().setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());

		MetadataControl.getInstance().init(frameworkControl.getMetadataRepositoryConfigurations()
				.stream().map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration))
				.collect(ArrayList::new, List::addAll, List::addAll));

		// Set up connection to the metadata repository
//		this.executionServerFilePath = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("run.exec")
//						+ File.separator + "ExecutionServerRepository.db3";
//
//		if (!ExecutionServerTools.getServerMode().equalsIgnoreCase("off")) {
//			if (!FileTools.exists(this.getExecutionServerFilePath())) {
//				throw new RuntimeException("framework.server.repository.notfound");
//			}
//		}

		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(this.getExecutionServerFilePath());
		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
		Map<String, Database> databases = new HashMap<>();
		databases.put("reader", sqliteDatabase);
		databases.put("writer", sqliteDatabase);
		databases.put("owner", sqliteDatabase);
		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
		this.executionServerRepositoryConfiguration = new ExecutionServerMetadataRepository(
				frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
				FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
				FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"));

		Executor.getInstance().init();
		Requestor.getInstance().init();

		FrameworkExecution.getInstance().init(context);
		// TODO: move Executor (Request to separate module)
		ExecutorService.getInstance();
	}

	// Getters and Setters
	public ExecutionServerMetadataRepository getExecutionServerRepositoryConfiguration() {
		return executionServerRepositoryConfiguration;
	}

	public String getExecutionServerFilePath() {
		return executionServerFilePath;
	}
}