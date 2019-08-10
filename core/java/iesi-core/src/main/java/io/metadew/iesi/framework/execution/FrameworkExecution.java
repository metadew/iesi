package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.definition.FrameworkRunIdentifier;
import org.apache.logging.log4j.ThreadContext;

public class FrameworkExecution {

    // private FrameworkInstance frameworkInstance;
    private FrameworkExecutionContext frameworkExecutionContext;
    private FrameworkExecutionSettings frameworkExecutionSettings;
    private FrameworkResultProvider frameworkResultProvider;

    private static FrameworkExecution INSTANCE;

    public synchronized static FrameworkExecution getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkExecution();
        }
        return INSTANCE;
    }

    private FrameworkExecution() {}

    public void init() {
        init(new FrameworkExecutionContext(), new FrameworkExecutionSettings(), new FrameworkRunIdentifier());
    }

    public void init(FrameworkExecutionContext frameworkExecutionContext) {
        init(frameworkExecutionContext, new FrameworkExecutionSettings(), new FrameworkRunIdentifier());
    }

    public void init(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings, FrameworkRunIdentifier frameworkRunIdentifier) {
        this.frameworkExecutionContext = frameworkExecutionContext;
        ThreadContext.put("context.name", frameworkExecutionContext.getContext().getName());
        ThreadContext.put("context.scope", frameworkExecutionContext.getContext().getScope());
        this.frameworkExecutionSettings = frameworkExecutionSettings;
        FrameworkControl.getInstance().setSettingsList(frameworkExecutionSettings.getSettingsList());
        FrameworkRuntime.getInstance().init(frameworkRunIdentifier);
        FrameworkLog.getInstance().init();
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

    public FrameworkExecutionContext getFrameworkExecutionContext() {
        return frameworkExecutionContext;
    }

    public FrameworkExecutionSettings getFrameworkExecutionSettings() {
        return frameworkExecutionSettings;
    }

    public FrameworkResultProvider getFrameworkResultProvider() {
        return frameworkResultProvider;
    }

}