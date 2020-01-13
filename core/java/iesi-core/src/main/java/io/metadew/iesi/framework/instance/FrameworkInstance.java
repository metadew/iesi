package io.metadew.iesi.framework.instance;

import io.metadew.iesi.framework.configuration.FrameworkActionTypeConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.runtime.ExecutorService;

import java.util.ArrayList;
import java.util.List;

public class FrameworkInstance {

    private static FrameworkInstance INSTANCE;

    public synchronized static FrameworkInstance getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkInstance();
        }
        return INSTANCE;
    }

    private FrameworkInstance() {
    }


    public void init() {
        init(new FrameworkInitializationFile(), new FrameworkExecutionContext(new Context("general", "")));
    }

    public void init(FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext context) {
        init("write", frameworkInitializationFile, context);
    }

    public void init(FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext context, String frameworkHome) {
        // Get the framework configuration
        FrameworkConfiguration frameworkConfiguration = FrameworkConfiguration.getInstance();
        frameworkConfiguration.init(frameworkHome);

        FrameworkCrypto.getInstance();

        // Set appropriate initialization file
        if (frameworkInitializationFile.getName().trim().isEmpty()) {
            frameworkInitializationFile = new FrameworkInitializationFile(frameworkConfiguration.getFrameworkCode() + "-conf.ini");
        }

        // Prepare configuration and shared Metadata
        FrameworkControl frameworkControl = FrameworkControl.getInstance();
        frameworkControl.init("write", frameworkInitializationFile);

        FrameworkActionTypeConfiguration.getInstance().setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());
        List<MetadataRepository> metadataRepositories = new ArrayList<>();

        for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : frameworkControl.getMetadataRepositoryConfigurations()) {
            metadataRepositories.addAll(metadataRepositoryConfiguration.toMetadataRepositories());

        }
        MetadataControl.getInstance().init(metadataRepositories);

        FrameworkExecution.getInstance().init(context);
        // TODO: move Executor (Request to separate module)
        ExecutorService.getInstance();
    }

    public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext context) {
        // Get the framework configuration
        FrameworkConfiguration frameworkConfiguration = FrameworkConfiguration.getInstance();
        frameworkConfiguration.init();

        FrameworkCrypto.getInstance();

        // Set appropriate initialization file
        if (frameworkInitializationFile.getName().trim().isEmpty()) {
            frameworkInitializationFile = new FrameworkInitializationFile(frameworkConfiguration.getFrameworkCode() + "-conf.ini");
        }

        // Prepare configuration and shared Metadata
        FrameworkControl frameworkControl = FrameworkControl.getInstance();
        frameworkControl.init(logonType, frameworkInitializationFile);

        FrameworkActionTypeConfiguration.getInstance().setActionTypesFromPlugins(frameworkControl.getFrameworkPluginConfigurationList());
        List<MetadataRepository> metadataRepositories = new ArrayList<>();

        for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : frameworkControl.getMetadataRepositoryConfigurations()) {
            metadataRepositories.addAll(metadataRepositoryConfiguration.toMetadataRepositories());

        }
        MetadataControl.getInstance().init(metadataRepositories);

        // Set up connection to the metadata repository
//		this.executionServerFilePath = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("run.exec")
//						+ File.separator + "ExecutionServerRepository.db3";
//
//		if (!ExecutionServerTools.getServerMode().equalsIgnoreCase("off")) {
//			if (!FileTools.exists(this.getExecutionServerFilePath())) {
//				throw new RuntimeException("framework.server.repository.notfound");
//			}
//		}

//		SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(this.getExecutionServerFilePath());
//		SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
//		Map<String, Database> databases = new HashMap<>();
//		databases.put("reader", sqliteDatabase);
//		databases.put("writer", sqliteDatabase);
//		databases.put("owner", sqliteDatabase);
//		RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
//		this.executionServerRepositoryConfiguration = new ExecutionServerMetadataRepository(
//				frameworkConfiguration.getFrameworkCode(), null, null, null, repositoryCoordinator,
//				FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"),
//				FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def"));


        FrameworkExecution.getInstance().init(context);
        // TODO: move Executor (Request to separate module)
        ExecutorService.getInstance();
    }

    public void shutdown() {
        for (MetadataRepository metadataRepository : MetadataControl.getInstance().getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}