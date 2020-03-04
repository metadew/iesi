package io.metadew.iesi.data.generation.output.control.rule;

import io.metadew.iesi.data.generation.execution.GenerationControlRuleExecution;
import io.metadew.iesi.data.generation.execution.GenerationControlRuleParameterExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.generation.GenerationControlRuleParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Print {

    private GenerationControlRuleExecution generationControlRuleExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationControlRuleTypeName = "print";
    private String output = "";

    private static final Logger LOGGER = LogManager.getLogger();
    // Parameters
    private GenerationControlRuleParameterExecution value;

    // Constructors
    public Print(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationControlRuleExecution generationControlRuleExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationControlRuleExecution(generationControlRuleExecution);
    }

    //
    public boolean execute() {
        LOGGER.warn("generation.control.rule.type=" + this.getGenerationControlRuleTypeName(), Level.INFO);

        // Reset Parameters
        this.setValue(new GenerationControlRuleParameterExecution(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getGenerationControlRuleTypeName(), "VALUE"));

        // Get Parameters
        for (GenerationControlRuleParameter generationControlRuleParameter : this.getGenerationControlRuleExecution().getGenerationControlRule()
                .getParameters()) {
            if (generationControlRuleParameter.getName().equalsIgnoreCase("value")) {
                this.getValue().setInputValue(generationControlRuleParameter.getValue());
            }
        }

        // Run the generation Control Rule
        this.setOutput(this.getValue().getValue());

        return true;
    }

    // Getters and Setters
    public String getGenerationControlRuleTypeName() {
        return generationControlRuleTypeName;
    }

    public void setGenerationControlRuleTypeName(String generationControlRuleTypeName) {
        this.generationControlRuleTypeName = generationControlRuleTypeName;
    }

    public GenerationControlRuleParameterExecution getValue() {
        return value;
    }

    public void setValue(GenerationControlRuleParameterExecution value) {
        this.value = value;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public GenerationControlRuleExecution getGenerationControlRuleExecution() {
        return generationControlRuleExecution;
    }

    public void setGenerationControlRuleExecution(GenerationControlRuleExecution generationControlRuleExecution) {
        this.generationControlRuleExecution = generationControlRuleExecution;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}