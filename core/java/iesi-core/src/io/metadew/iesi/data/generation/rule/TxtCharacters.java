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

public class TxtCharacters {

	private GenerationRuleExecution generationRuleExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private String generationRuleTypeName = "txt.characters";

	// Parameters
	private GenerationRuleParameterExecution characterNumber;

	// Constructors
	public TxtCharacters() {
		
	}
	
	public TxtCharacters(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
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
			this.setCharacterNumber(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "CHAR_NB"));

			// Get Parameters
			for (GenerationRuleParameter generationRuleParameter : this.getGenerationRuleExecution().getGenerationRule()
					.getParameters()) {
				if (generationRuleParameter.getName().equalsIgnoreCase("char_nb")) {
					this.getCharacterNumber().setInputValue(generationRuleParameter.getValue());
				}
			}
			
			// Run the generationRule
			try {				
				for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution().getNumberOfRecords(); currentRecord++) {
					String generatedValue = "";
					if (this.getCharacterNumber().getValue().trim().equalsIgnoreCase("")) {
						generatedValue = this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getGenerationObjectExecution().getLorem().characters();
					} else {
						generatedValue = this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getGenerationObjectExecution().getLorem().characters(Integer.parseInt(this.getCharacterNumber().getValue()));
					}
					
					String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
					query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
					query += SQLTools.GetStringForSQL(generatedValue);
					query += " where id=" + (currentRecord + 1);
					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection().executeUpdate(query);
					
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

	public GenerationRuleParameterExecution getCharacterNumber() {
		return characterNumber;
	}

	public void setCharacterNumber(GenerationRuleParameterExecution characterNumber) {
		this.characterNumber = characterNumber;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}