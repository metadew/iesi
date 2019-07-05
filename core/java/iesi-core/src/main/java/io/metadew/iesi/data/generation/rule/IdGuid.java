package io.metadew.iesi.data.generation.rule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.data.generation.execution.GenerationRuleExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

public class IdGuid {

	private GenerationRuleExecution generationRuleExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters

	// Constructors
	public IdGuid() {
		
	}
	
	public IdGuid(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
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
			// Reset Parameters

			// Get Parameters

			// Run the generationRule
			try {
				
				for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution()
						.getNumberOfRecords(); currentRecord++) {

					UUID uuid = UUID.randomUUID();
					String generatedValue = uuid.toString();;
					
					String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
					query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
					query += SQLTools.GetStringForSQL(generatedValue);
					query += " where id=" + (currentRecord + 1);
					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection()
							.executeUpdate(query);
					
					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().updateProgress();
				}

			} catch (Exception e) {
				throw new RuntimeException("Issue setting runtime variables: " + e, e);
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}