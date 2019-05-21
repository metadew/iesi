package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata_repository.ExecutionServerMetadataRepository;
import io.metadew.iesi.metadata_repository.repository.Repository;
import io.metadew.iesi.metadata_repository.repository.database.Database;
import io.metadew.iesi.metadata_repository.repository.database.SqliteDatabase;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkExecution {

    private FrameworkConfiguration frameworkConfiguration;
    private FrameworkExecutionContext frameworkExecutionContext;
    private FrameworkExecutionSettings frameworkExecutionSettings;
    private FrameworkCrypto frameworkCrypto;
    private FrameworkControl frameworkControl;
    private FrameworkLog frameworkLog;
    private MetadataControl metadataControl;
    private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;
    private FrameworkInitializationFile frameworkInitializationFile;


    // Constructors
    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkInitializationFile frameworkInitializationFile) {
        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), "write", frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, FrameworkInitializationFile frameworkInitializationFile) {
        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, "write", frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, String logonType, FrameworkInitializationFile frameworkInitializationFile) {
        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), logonType, frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, String logonType, FrameworkInitializationFile frameworkInitializationFile) {
        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, logonType, frameworkInitializationFile);
    }

    // Methods
    private void initializeFrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, String logonType, FrameworkInitializationFile frameworkInitializationFile) {
        // Set the execution context
        this.setFrameworkExecutionContext(frameworkExecutionContext);
        this.setFrameworkExecutionSettings(frameworkExecutionSettings);

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
        this.setFrameworkControl(new FrameworkControl(this.getFrameworkConfiguration(), logonType, this.getFrameworkInitializationFile()));
        this.setMetadataControl(new MetadataControl(this.getFrameworkControl().getMetadataRepositoryConfigurations().stream().map(configuration -> configuration.toMetadataRepositories(frameworkConfiguration)).collect(ArrayList::new, List::addAll, List::addAll)));

        this.setSettingsList(this.getFrameworkExecutionSettings().getSettingsList());
        this.setFrameworkLog(new FrameworkLog(this.getFrameworkConfiguration(), this.getFrameworkExecutionContext(), this.getFrameworkControl(), this.getFrameworkCrypto()));

        // Set up connection to the metadata repository
        SqliteDatabaseConnection executionServerDatabaseConnection = new SqliteDatabaseConnection(
                this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.exec") + File.separator + "ExecutionServerRepository.db3");
        SqliteDatabase sqliteDatabase = new SqliteDatabase(executionServerDatabaseConnection);
        Map<String, Database> databases = new HashMap<>();
        databases.put("reader", sqliteDatabase);
        databases.put("writer", sqliteDatabase);
        databases.put("owner", sqliteDatabase);
        Repository repository = new Repository(databases);
        this.setExecutionServerRepositoryConfiguration(new ExecutionServerMetadataRepository(frameworkConfiguration.getFrameworkCode(), null, null, null, repository,
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

    public FrameworkExecutionSettings getFrameworkExecutionSettings() {
        return frameworkExecutionSettings;
    }

    public void setFrameworkExecutionSettings(FrameworkExecutionSettings frameworkExecutionSettings) {
        this.frameworkExecutionSettings = frameworkExecutionSettings;
    }

    public FrameworkCrypto getFrameworkCrypto() {
        return frameworkCrypto;
    }

    public void setFrameworkCrypto(FrameworkCrypto frameworkCrypto) {
        this.frameworkCrypto = frameworkCrypto;
    }

    public FrameworkLog getFrameworkLog() {
        return frameworkLog;
    }

    public void setFrameworkLog(FrameworkLog frameworkLog) {
        this.frameworkLog = frameworkLog;
    }

    public MetadataControl getMetadataControl() {
        return metadataControl;
    }

    public void setMetadataControl(MetadataControl metadataControl) {
        this.metadataControl = metadataControl;
    }

    public FrameworkExecutionContext getFrameworkExecutionContext() {
        return frameworkExecutionContext;
    }

    public void setFrameworkExecutionContext(FrameworkExecutionContext frameworkExecutionContext) {
        this.frameworkExecutionContext = frameworkExecutionContext;
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