package io.metadew.iesi.data.generation.rule;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.generation.execution.GenerationRuleExecution;
import io.metadew.iesi.data.generation.execution.GenerationRuleParameterExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationRuleParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ValPattern {

	private GenerationRuleExecution generationRuleExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private String generationRuleTypeName = "val.pattern";

	// Parameters
	private GenerationRuleParameterExecution expression;

	// Constructors
	public ValPattern() {
		
	}
	
	public ValPattern(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
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
		try {
			this.getFrameworkExecution().getFrameworkLog()
					.log("generation.rule.type=" + this.getGenerationRuleTypeName(), Level.INFO);

			// Reset Parameters
			this.setExpression(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "expression"));

			// Get Parameters
			for (GenerationRuleParameter generationRuleParameter : this.getGenerationRuleExecution().getGenerationRule()
					.getParameters()) {
				if (generationRuleParameter.getName().equalsIgnoreCase("expression")) {
					this.getExpression().setInputValue(generationRuleParameter.getValue());
				}
			}

			// Run the generationRule
			try {
				for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution()
						.getNumberOfRecords(); currentRecord++) {
					
					String generatedValue = this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime()
							.getGenerationObjectExecution().getPattern().nextValue(this.getExpression().getValue());
					
					String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
					query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
					query += SQLTools.GetStringForSQL(generatedValue);
					query += " where id=" + (currentRecord + 1);
					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection()
							.executeUpdate(query);

					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().updateProgress();
				}

			} catch (Exception e) {
				throw new RuntimeException("Issue generating data: " + e, e);
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

	public GenerationRuleParameterExecution getExpression() {
		return expression;
	}

	public void setExpression(GenerationRuleParameterExecution expression) {
		this.expression = expression;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}