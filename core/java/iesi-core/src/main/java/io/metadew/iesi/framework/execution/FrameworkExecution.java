package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.definition.FrameworkRunIdentifier;
import io.metadew.iesi.metadata.repository.ExecutionServerMetadataRepository;
import org.apache.logging.log4j.ThreadContext;

public class FrameworkExecution {

    // private FrameworkInstance frameworkInstance;
    private FrameworkExecutionContext frameworkExecutionContext;
    private FrameworkExecutionSettings frameworkExecutionSettings;
    private FrameworkResultProvider frameworkResultProvider;
    private FrameworkRuntime frameworkRuntime;
    private ExecutionServerMetadataRepository executionServerRepositoryConfiguration;

    public FrameworkExecution() {
        this(new FrameworkExecutionContext(), new FrameworkExecutionSettings(), new FrameworkRunIdentifier());
    }

    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext) {
        this(frameworkExecutionContext, new FrameworkExecutionSettings(), new FrameworkRunIdentifier());
    }

    public FrameworkExecution(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, FrameworkRunIdentifier frameworkRunIdentifier) {
        this.frameworkExecutionContext = frameworkExecutionContext;
        this.frameworkExecutionSettings = frameworkExecutionSettings;
        FrameworkControl.getInstance().setSettingsList(frameworkExecutionSettings.getSettingsList());
        this.frameworkRuntime = new FrameworkRuntime(frameworkRunIdentifier);
        FrameworkLog frameworkLog = FrameworkLog.getInstance();
        frameworkLog.init();
        this.frameworkResultProvider = new FrameworkResultProvider();
    }


//    // Constructors
//    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
//                              FrameworkInitializationFile frameworkInitializationFile) {
////        this(frameworkExecutionContext, new FrameworkExecutionSettings(""));
//        this.setFrameworkInstance(frameworkInstance);
//        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), "write",
//                frameworkInitializationFile,null);
//    }
//
//    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext, String logonType,
//                              FrameworkInitializationFile frameworkInitializationFile) {
//        this.setFrameworkInstance(frameworkInstance);
//        this.initializeFrameworkExecution(frameworkExecutionContext, new FrameworkExecutionSettings(""), logonType,
//                frameworkInitializationFile, null);
//    }
//
//    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
//                              FrameworkExecutionSettings frameworkExecutionSettings,
//                              FrameworkInitializationFile frameworkInitializationFile, FrameworkRunIdentifier frameworkRunIdentifier) {
//        this.setFrameworkInstance(frameworkInstance);
//        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, "write",
//                frameworkInitializationFile, frameworkRunIdentifier);
//    }
//
//    public FrameworkExecution(FrameworkInstance frameworkInstance, FrameworkExecutionContext frameworkExecutionContext,
//                              FrameworkExecutionSettings frameworkExecutionSettings, String logonType,
//                              FrameworkInitializationFile frameworkInitializationFile) {
//        this.setFrameworkInstance(frameworkInstance);
//        this.initializeFrameworkExecution(frameworkExecutionContext, frameworkExecutionSettings, logonType,
//                frameworkInitializationFile, null);
//    }

//    // Methods
//    private void initializeFrameworkExecution(FrameworkExecutionContext frameworkExecutionContext,
//                                              FrameworkExecutionSettings frameworkExecutionSettings, String logonType,
//                                              FrameworkInitializationFile frameworkInitializationFile, FrameworkRunIdentifier frameworkRunIdentifier) {
//        // Set the execution context
//        this.setFrameworkExecutionContext(frameworkExecutionContext);
//        this.setFrameworkExecutionSettings(frameworkExecutionSettings);
//
//        // Maintain backward compatibility
//        if (this.getFrameworkInstance() == null) {
//            this.setFrameworkInstance(new FrameworkInstance(logonType, frameworkInitializationFile));
//        }
//
//        // Bind framework instance
//        this.setFrameworkConfiguration(this.getFrameworkInstance().getFrameworkConfiguration());
//        this.setFrameworkCrypto(this.getFrameworkInstance().getFrameworkCrypto());
//        this.setFrameworkControl(this.getFrameworkInstance().getFrameworkControl());
//        this.setMetadataControl(this.getFrameworkInstance().getMetadataControl());
//        this.setExecutionServerRepositoryConfiguration(
//                this.getFrameworkInstance().getExecutionServerRepositoryConfiguration());
//
//        // Settings list
//        this.setSettingsList(this.getFrameworkExecutionSettings().getSettingsList());
//
//        // Setup framework runtime
//        this.setFrameworkRuntime(new FrameworkRuntime(frameworkRunIdentifier));
//        FrameworkLog frameworkLog = FrameworkLog.getInstance();
//        frameworkLog.init();
//        this.setFrameworkResultProvider(new FrameworkResultProvider());
//    }

    public void setSettingsList(String input) {
        FrameworkControl.getInstance().setSettingsList(input);
    }

    // Getters and Setters
    public ExecutionServerMetadataRepository getExecutionServerRepositoryConfiguration() {
        return executionServerRepositoryConfiguration;
    }

    public void setExecutionServerRepositoryConfiguration(
            ExecutionServerMetadataRepository executionServerRepositoryConfiguration) {
        this.executionServerRepositoryConfiguration = executionServerRepositoryConfiguration;
    }

    public FrameworkExecutionSettings getFrameworkExecutionSettings() {
        return frameworkExecutionSettings;
    }

    public void setFrameworkExecutionSettings(FrameworkExecutionSettings frameworkExecutionSettings) {
        this.frameworkExecutionSettings = frameworkExecutionSettings;
    }

    public FrameworkExecutionContext getFrameworkExecutionContext() {
        return frameworkExecutionContext;
    }

    public void setFrameworkExecutionContext(FrameworkExecutionContext frameworkExecutionContext) {
        this.frameworkExecutionContext = frameworkExecutionContext;
        ThreadContext.put("context.name", frameworkExecutionContext.getContext().getName());
        ThreadContext.put("context.scope", frameworkExecutionContext.getContext().getScope());
    }

    public FrameworkRuntime getFrameworkRuntime() {
        return frameworkRuntime;
    }

    public void setFrameworkRuntime(FrameworkRuntime frameworkRuntime) {
        this.frameworkRuntime = frameworkRuntime;
        ThreadContext.put("fwk.runid", frameworkRuntime.getRunId());
    }

	public FrameworkResultProvider getFrameworkResultProvider() {
		return frameworkResultProvider;
	}

	public void setFrameworkResultProvider(FrameworkResultProvider frameworkResultProvider) {
		this.frameworkResultProvider = frameworkResultProvider;
	}
}