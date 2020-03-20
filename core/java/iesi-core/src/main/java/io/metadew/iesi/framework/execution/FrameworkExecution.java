package io.metadew.iesi.framework.execution;

import org.apache.logging.log4j.ThreadContext;

public class FrameworkExecution {

    // private FrameworkInstance frameworkInstance;
    private FrameworkExecutionContext frameworkExecutionContext;
    private FrameworkExecutionSettings frameworkExecutionSettings;

    private static FrameworkExecution INSTANCE;

    public synchronized static FrameworkExecution getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkExecution();
        }
        return INSTANCE;
    }

    private FrameworkExecution() {}

    public void init() {
        init(new FrameworkExecutionContext());
    }

    public void init(FrameworkExecutionContext frameworkExecutionContext) {
        init(frameworkExecutionContext, new FrameworkExecutionSettings());
    }

    public void init(FrameworkExecutionContext frameworkExecutionContext, FrameworkExecutionSettings frameworkExecutionSettings) {
            this.frameworkExecutionContext = frameworkExecutionContext;
        ThreadContext.put("context.name", frameworkExecutionContext.getContext().getName());
        ThreadContext.put("context.scope", frameworkExecutionContext.getContext().getScope());
        this.frameworkExecutionSettings = frameworkExecutionSettings;
        FrameworkRuntime.getInstance().init();
        FrameworkLog.getInstance().init();
    }

    public FrameworkExecutionContext getFrameworkExecutionContext() {
        return frameworkExecutionContext;
    }

}