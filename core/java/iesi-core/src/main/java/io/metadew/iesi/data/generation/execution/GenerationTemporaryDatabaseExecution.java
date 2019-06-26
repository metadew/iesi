package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;

public class GenerationTemporaryDatabaseExecution {

    private FrameworkExecution frameworkExecution;


    // Constructors
    public GenerationTemporaryDatabaseExecution(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }


    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}