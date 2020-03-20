package io.metadew.iesi.framework.execution;

import io.metadew.iesi.metadata.definition.Context;

/**
 * The execution context contains contextual information when any execution process starts and maintains it during processing.
 *
 * @author peter.billen
 */
public class FrameworkExecutionContext {

    private Context context;

    public FrameworkExecutionContext() {
        this(new Context());
    }
    public FrameworkExecutionContext(Context context) {
        this.context = context;
    }

    // Getters and setters
    public Context getContext() {
        return context;
    }


}