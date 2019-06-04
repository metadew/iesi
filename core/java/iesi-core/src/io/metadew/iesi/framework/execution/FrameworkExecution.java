package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;

public class FrameworkExecution {

    private FrameworkInstance frameworkInstance;
    private FrameworkConfiguration frameworkConfiguration;
    private FrameworkExecutionContext frameworkExecutionContext;
    private FrameworkExecutionSettings frameworkExecutionSettings;
    private FrameworkCrypto frameworkCrypto;
    private FrameworkControl frameworkControl;
    private FrameworkLog frameworkLog;
    private FrameworkResultProvider frameworkResultProvider;
    private FrameworkRuntime frameworkRuntime;
    private MetadataControl metadataControl;
    private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;
    private FrameworkInitializationFile frameworkInitializationFile;

    // Constructors
    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
                              FrameworkInitializationFile frameworkInitializationFile) {
        this.setFrameworkInstance(frameworkInstance);
        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), "write",
                frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext, String logonType,
                              FrameworkInitializationFile frameworkInitializationFile) {
        this.setFrameworkInstance(frameworkInstance);
        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), logonType,
                frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
                              FrameworkExecutionSettings frameworkExecutionSettings,
                              FrameworkInitializationFile frameworkInitializationFile) {
        this.setFrameworkInstance(frameworkInstance);
        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, "write",
                frameworkInitializationFile);
    }

    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
                              FrameworkExecutionSettings frameworkExecutionSettings, String logonType,
                              FrameworkInitializationFile frameworkInitializationFile) {
        this.setFrameworkInstance(frameworkInstance);
        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, logonType,
                frameworkInitializationFile);
    }

    // Methods
    private void initializeFrameworkExecution(FrameworkExecutionContext frameworkExecutionContext,
                                              FrameworkExecutionSettings frameworkExecutionSettings, String logonType,
                                              FrameworkInitializationFile frameworkInitializationFile) {
        // Set the execution context
        this.setFrameworkExecutionContext(frameworkExecutionContext);
        this.setFrameworkExecutionSettings(frameworkExecutionSettings);

        // Maintain backward compatibility
        if (this.getFrameworkInstance() == null) {
            this.setFrameworkInstance(new FrameworkInstance(logonType, frameworkInitializationFile));
        }

        // Bind framework instance
        this.setFrameworkConfiguration(this.getFrameworkInstance().getFrameworkConfiguration());
        this.setFrameworkCrypto(this.getFrameworkInstance().getFrameworkCrypto());
        this.setFrameworkControl(this.getFrameworkInstance().getFrameworkControl());
        this.setMetadataControl(this.getFrameworkInstance().getMetadataControl());
        this.setExecutionServerRepositoryConfiguration(
                this.getFrameworkInstance().getExecutionServerRepositoryConfiguration());

        // Settings list
        this.setSettingsList(this.getFrameworkExecutionSettings().getSettingsList());

        // Setup framework runtime
        this.setFrameworkRuntime(new FrameworkRuntime(this.getFrameworkConfiguration()));
        this.setFrameworkLog(new FrameworkLog(this.getFrameworkConfiguration(), this.getFrameworkExecutionContext(),
                this.getFrameworkControl(), this.getFrameworkCrypto(), this.getFrameworkRuntime()));
        this.setFrameworkResultProvider(new FrameworkResultProvider());
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

    public FrameworkRuntime getFrameworkRuntime() {
        return frameworkRuntime;
    }

    public void setFrameworkRuntime(FrameworkRuntime frameworkRuntime) {
        this.frameworkRuntime = frameworkRuntime;
    }

    public FrameworkInstance getFrameworkInstance() {
        return frameworkInstance;
    }

    public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
        this.frameworkInstance = frameworkInstance;
    }

	public FrameworkResultProvider getFrameworkResultProvider() {
		return frameworkResultProvider;
	}

	public void setFrameworkResultProvider(FrameworkResultProvider frameworkResultProvider) {
		this.frameworkResultProvider = frameworkResultProvider;
	}
}