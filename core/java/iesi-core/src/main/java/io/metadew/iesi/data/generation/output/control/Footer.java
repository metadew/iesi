package io.metadew.iesi.data.generation.output.control;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.data.generation.execution.GenerationControlExecution;
import io.metadew.iesi.data.generation.execution.GenerationControlRuleExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Footer {

    private GenerationControlExecution generationControlExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationControlTypeName = "footer";

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
        try {
            this.getFrameworkExecution().getFrameworkLog()
                    .log("generation.control.type=" + this.generationControlTypeName, Level.INFO);

            // Reset Parameters


            // Get Parameters


            // Run the generation control
            try {
                StringBuilder footer = new StringBuilder();

                for (GenerationControlRule generationControlRule : this.getGenerationControlExecution().getGenerationControl().getRules()) {
                    GenerationControlRuleExecution generationControlRuleExecution = new GenerationControlRuleExecution(this.getFrameworkExecution(), this.getExecutionControl(), this.getGenerationControlExecution().getGenerationExecution(), generationControlRule);
                    generationControlRuleExecution.execute();
                    footer.append(generationControlRuleExecution.getOutput());
                }

                FileTools.appendToFile(fullFileName, "", footer.toString());

            } catch (Exception e) {
                throw new RuntimeException("Issue generating output: " + e, e);
            }

            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            // TODO logging

            return false;
        }

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