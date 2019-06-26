package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerationRuleBlankInjectionExecution {

    private GenerationRuleExecution generationRuleExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationRuleTypeName = "NUMBER";

    // Constructors
    public GenerationRuleBlankInjectionExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                                 GenerationRuleExecution generationRuleExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationRuleExecution(generationRuleExecution);
    }

    //
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean execute() {
        try {
            this.getFrameworkExecution().getFrameworkLog().log("generation.rule.blankinjection", Level.INFO);

            // Run the injection
            try {
                // Create list with possible records
                List<String> list = new ArrayList();
                for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution()
                        .getNumberOfRecords(); currentRecord++) {
                    list.add(String.valueOf(currentRecord + 1));
                }

                // Define number of blanks
                long numberOfBlanks = 0;
                if (this.getGenerationRuleExecution().getGenerationRule().getBlankInjectionUnit().trim().toLowerCase()
                        .equalsIgnoreCase("number")) {
                    numberOfBlanks = this.getGenerationRuleExecution().getGenerationRule().getBlankInjectionMeasure();
                } else if (this.getGenerationRuleExecution().getGenerationRule().getBlankInjectionUnit().trim().toLowerCase()
                        .equalsIgnoreCase("percentage")) {
                    float temp = (float) this.getGenerationRuleExecution().getGenerationRule().getBlankInjectionMeasure()
                            * this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords() / 100;
                    numberOfBlanks = Math.round(temp);
                } else {
                    numberOfBlanks = 0;
                }

                // Cap at maximum of generated records
                if (numberOfBlanks > this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords())
                    numberOfBlanks = this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords();

                // Get blank value
                String blankValue = this.getGenerationRuleExecution().getGenerationRule().getBlankInjectionValue();

                // Generate blanks
                for (int currentBlank = 0; currentBlank < numberOfBlanks; currentBlank++) {
                    long selectedBlank = this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime()
                            .getGenerationObjectExecution().getNumber().getNextLong(0,
                                    this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords() - currentBlank - 1);

                    String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
                    query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
                    query += SQLTools.GetStringForSQL(blankValue);
                    query += " where id=" + (list.get((int) selectedBlank));

                    this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection()
                            .executeUpdate(query);

                    list.remove((int) selectedBlank);

                }


            } catch (Exception e) {
                throw new RuntimeException("Issue injecting blank values: " + e, e);
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
    public GenerationRuleExecution getGenerationRuleExecution() {
        return generationRuleExecution;
    }

    public void setGenerationRuleExecution(GenerationRuleExecution generationRuleExecution) {
        this.generationRuleExecution = generationRuleExecution;
    }

    public String getGenerationRuleTypeName() {
        return generationRuleTypeName;
    }

    public void setGenerationRuleTypeName(String generationRuleTypeName) {
        this.generationRuleTypeName = generationRuleTypeName;
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