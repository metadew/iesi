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
import java.text.SimpleDateFormat;

public class DtDate {

	private GenerationRuleExecution generationRuleExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private String generationRuleTypeName = "dt.date";
	
	//Defaults
	 private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

	// Parameters
	private GenerationRuleParameterExecution minimumValue;
	private GenerationRuleParameterExecution maximumValue;
	private GenerationRuleParameterExecution format;

	// Constructors
	public DtDate() {
		
	}
	
	public DtDate(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
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
			this.setMinimumValue(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "MIN_VALUE"));
			this.setMaximumValue(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "MAX_VALUE"));
			this.setFormat(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "FORMAT"));

			// Get Parameters
			for (GenerationRuleParameter generationRuleParameter : this.getGenerationRuleExecution().getGenerationRule()
					.getParameters()) {
				if (generationRuleParameter.getName().equalsIgnoreCase("min_value")) {
					this.getMinimumValue().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("max_value")) {
					this.getMaximumValue().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("format")) {
					this.getFormat().setInputValue(generationRuleParameter.getValue());
				}
			}

			// Run the generationRule
			try {
				for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution()
						.getNumberOfRecords(); currentRecord++) {

					String generatedValue = "";

					// Set format
					SimpleDateFormat dateFormat = null;
					if (this.getFormat().getValue().trim().equalsIgnoreCase("")) {
						dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);	
					} else {
						dateFormat = new SimpleDateFormat(this.getFormat().getValue());
					}

					// Generate value
					generatedValue = dateFormat
							.format(this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime()
									.getGenerationObjectExecution().getTimestamp().getNextTimestamp(
											this.getMinimumValue().getValue(), this.getMaximumValue().getValue()))
							.toString();

					String query = "update " + this.getGenerationRuleExecution().getGenerationExecution().getGeneration().getName();
					query += " set v" + this.getGenerationRuleExecution().getGenerationRule().getField() + "=";
					query += SQLTools.GetStringForSQL(generatedValue);
					query += " where id=" + (currentRecord + 1);
					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().getTemporaryDatabaseConnection()
							.executeUpdate(query);

					this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime().updateProgress();
				}

			} catch (Exception e) {
				throw new RuntimeException("Issue setting test data: " + e, e);
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

	public GenerationRuleParameterExecution getMinimumValue() {
		return minimumValue;
	}

	public void setMinimumValue(GenerationRuleParameterExecution minimumValue) {
		this.minimumValue = minimumValue;
	}

	public GenerationRuleParameterExecution getMaximumValue() {
		return maximumValue;
	}

	public void setMaximumValue(GenerationRuleParameterExecution maximumValue) {
		this.maximumValue = maximumValue;
	}

	public GenerationRuleParameterExecution getFormat() {
		return format;
	}

	public void setFormat(GenerationRuleParameterExecution format) {
		this.format = format;
	}

	public static String getDefaultFormat() {
		return DEFAULT_FORMAT;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}