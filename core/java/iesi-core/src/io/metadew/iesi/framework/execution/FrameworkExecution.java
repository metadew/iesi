package io.metadew.iesi.framework.execution;

import java.io.File;

import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.execution.MetadataControl;

public class FrameworkExecution {

	private FrameworkConfiguration frameworkConfiguration;
	private FrameworkExecutionContext frameworkExecutionContext;
	private FrameworkExecutionSettings frameworkExecutionSettings;
	private FrameworkCrypto frameworkCrypto;
	private FrameworkControl frameworkControl;
	private FrameworkLog frameworkLog;
	private MetadataControl metadataControl;
	private MetadataRepositoryConfiguration executionServerRepositoryConfiguration;
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
		this.setMetadataControl(new MetadataControl(this.getFrameworkControl().getMetadataRepositoryConfigurationList()));
		
		this.setSettingsList(this.getFrameworkExecutionSettings().getSettingsList());
		this.setFrameworkLog(new FrameworkLog(this.getFrameworkConfiguration(), this.getFrameworkExecutionContext(), this.getFrameworkControl(), this.getFrameworkCrypto()));

		// Set up connection to the metadata repository
		SqliteDatabaseConnection executionServerRepositoryConnection = new SqliteDatabaseConnection(
				this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.exec") + File.separator + "ExecutionServerRepository.db3");
		this.setExecutionServerRepositoryConfiguration(
				new MetadataRepositoryConfiguration(this.getFrameworkConfiguration(), this.frameworkControl, executionServerRepositoryConnection));

	}

	public void setSettingsList(String input) {
		this.getFrameworkControl().setSettingsList(input);
	}

	// Getters and Setters
	public MetadataRepositoryConfiguration getExecutionServerRepositoryConfiguration() {
		return executionServerRepositoryConfiguration;
	}

	public void setExecutionServerRepositoryConfiguration(
			MetadataRepositoryConfiguration executionServerRepositoryConfiguration) {
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