package io.metadew.iesi.data.generation.rule;

import io.metadew.iesi.connection.database.DatabaseHandlerImpl;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.generation.execution.GenerationRuleExecution;
import io.metadew.iesi.data.generation.execution.GenerationRuleParameterExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.generation.GenerationRuleParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TxtParagraphs {

    private GenerationRuleExecution generationRuleExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationRuleTypeName = "txt.paragraphs";
    private static final Logger LOGGER = LogManager.getLogger();

    // Parameters
    private GenerationRuleParameterExecution paragraphNumber;

    // Constructors
    public TxtParagraphs() {

    }

    public TxtParagraphs(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setEoControl(executionControl);
        this.setGenerationRuleExecution(generationRuleExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setEoControl(executionControl);
        this.setGenerationRuleExecution(generationRuleExecution);
    }

    //
    public boolean execute() {
        LOGGER.info("generation.rule.type=" + this.getGenerationRuleTypeName());

        // Reset Parameters
        this.setParagraphNumber(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
                this.getGenerationRuleTypeName(), "PARAGRAPH_NB"));

        // Get Parameters
        for (GenerationRuleParameter generationRuleParameter : this.getGenerationRuleExecution().getGenerationRule()
                .getParameters()) {
            if (generationRuleParameter.getName().equalsIgnoreCase("paragraph_nb")) {
                this.getParagraphNumber().setInputValue(generationRuleParameter.getValue());
            }
        }

        // Run the generationRule
        for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords(); currentRecord++) {
            String generatedValue = "";
            int numberOfParagraphs = 0;
            if (this.getParagraphNumber().getValue().trim().equalsIgnoreCase("")) {
                numberOfParagraphs = this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getGenerationObjectExecution().getGenerationTools().getRandomTools().range(1, 10);
            } else {
                numberOfParagraphs = Integer.parseInt(this.getParagraphNumber().getValue());
            }

            for (int currentParagraph = 0; currentParagraph < numberOfParagraphs; currentParagraph++) {
                if (currentParagraph > 0) generatedValue += " ";
                generatedValue += this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getGenerationObjectExecution().getLorem().paragraph();
            }

            String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
            query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
            query += SQLTools.GetStringForSQL(generatedValue);
            query += " where id=" + (currentRecord + 1);
            DatabaseHandlerImpl.getInstance().executeUpdate(this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection(), query);

            this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().updateProgress();
        }


        return true;

    }

    // Getters and Setters
    public ExecutionControl getEoControl() {
        return executionControl;
    }

    public void setEoControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

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

    public GenerationRuleParameterExecution getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(GenerationRuleParameterExecution paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }
}