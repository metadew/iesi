package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.data.generation.output.control.Footer;
import io.metadew.iesi.data.generation.output.control.Header;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControl;
import io.metadew.iesi.script.execution.ExecutionControl;

public class GenerationControlExecution {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private GenerationExecution generationExecution;
    private GenerationControl generationControl;

    // Constructors
    public GenerationControlExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationExecution generationExecution, String generationControlName) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationExecution(generationExecution);
        for (GenerationControl generationControl : this.getGenerationExecution().getGeneration().getControls()) {
            if (generationControl.getName().trim().equalsIgnoreCase(generationControlName.trim())) {
                this.setGenerationControl(generationControl);
            }
        }
    }

    // Methods
    public void execute(String fullFileName) {

        try {
            if (this.getGenerationControl().getType().trim().equalsIgnoreCase("footer")) {
                Footer executionControlFooter = new Footer(this.getFrameworkExecution(), this.getExecutionControl(), this);
                executionControlFooter.execute(fullFileName);
            } else if (this.getGenerationControl().getType().trim().equalsIgnoreCase("header")) {
                Header executionControlHeader = new Header(this.getFrameworkExecution(), this.getExecutionControl(), this);
                executionControlHeader.execute(fullFileName);
            } else {

            }
        } catch (Exception e) {

        } finally {

        }

    }

    // Getters and Setters
    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public GenerationExecution getGenerationExecution() {
        return generationExecution;
    }

    public void setGenerationExecution(GenerationExecution generationExecution) {
        this.generationExecution = generationExecution;
    }

    public GenerationControl getGenerationControl() {
        return generationControl;
    }

    public void setGenerationControl(GenerationControl generationControl) {
        this.generationControl = generationControl;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }


}