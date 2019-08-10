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