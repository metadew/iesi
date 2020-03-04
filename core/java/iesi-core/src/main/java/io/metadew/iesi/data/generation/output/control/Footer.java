package io.metadew.iesi.data.generation.output.control;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.data.generation.execution.GenerationControlExecution;
import io.metadew.iesi.data.generation.execution.GenerationControlRuleExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.generation.GenerationControlRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Footer {

    private GenerationControlExecution generationControlExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationControlTypeName = "footer";
    private static final Logger LOGGER = LogManager.getLogger();

    // Parameters

    // Constructors
    public Footer(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                  GenerationControlExecution generationControlExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationControlExecution(generationControlExecution);
    }

    //
    public boolean execute(String fullFileName) {
        LOGGER.info("generation.control.type=" + this.generationControlTypeName);

        // Reset Parameters


        // Get Parameters


        // Run the generation control
        StringBuilder footer = new StringBuilder();

        for (GenerationControlRule generationControlRule : this.getGenerationControlExecution().getGenerationControl().getRules()) {
            GenerationControlRuleExecution generationControlRuleExecution = new GenerationControlRuleExecution(this.getFrameworkExecution(), this.getExecutionControl(), this.getGenerationControlExecution().getGenerationExecution(), generationControlRule);
            generationControlRuleExecution.execute();
            footer.append(generationControlRuleExecution.getOutput());
        }

        FileTools.appendToFile(fullFileName, "", footer.toString());


        return true;

    }

    // Getters and Setters
    public String getGenerationControlTypeName() {
        return generationControlTypeName;
    }

    public void setGenerationControlTypeName(String generationControlTypeName) {
        this.generationControlTypeName = generationControlTypeName;
    }

    public GenerationControlExecution getGenerationControlExecution() {
        return generationControlExecution;
    }

    public void setGenerationControlExecution(GenerationControlExecution generationControlExecution) {
        this.generationControlExecution = generationControlExecution;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}