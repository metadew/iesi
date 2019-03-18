package io.metadew.iesi.framework.execution;

import java.io.File;

import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
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


	// Constructors
	public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext) {
		this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), "write");
	}

	public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings) {
		this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, "write");
	}

	public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, String logonType) {
		this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), logonType);
	}

	public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, String logonType) {
		this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, logonType);
	}
	
	// Methods
	private void initializeFrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, String logonType) {
		// Set the execution context
		this.setFrameworkExecutionContext(frameworkExecutionContext);
		this.setFrameworkExecutionSettings(frameworkExecutionSettings);

		// Get the framework configuration
		this.setFrameworkConfiguration(new FrameworkConfiguration());
		this.setFrameworkCrypto(new FrameworkCrypto());
		
		// Prepare configuration and shared Metadata
		this.setFrameworkControl(new FrameworkControl(this.getFrameworkConfiguration(), logonType));
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
}