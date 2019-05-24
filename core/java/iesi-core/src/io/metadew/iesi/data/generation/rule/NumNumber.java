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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumNumber {

	private GenerationRuleExecution generationRuleExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;
	private String generationRuleTypeName = "num.number";

	// Defaults
	private static final int DEFAULT_DECIMAL_NUMBER = 2;
	private static final String DEFAULT_DECIMAL_CHAR = ".";

	// Parameters
	private GenerationRuleParameterExecution minimumValue;
	private GenerationRuleParameterExecution maximumValue;
	private GenerationRuleParameterExecution decimalFlag;
	private GenerationRuleParameterExecution decimalNumber;
	private GenerationRuleParameterExecution decimalChar;

	// Constructors
	public NumNumber() {
		
	}
	
	public NumNumber(FrameworkExecution frameworkExecution, ExecutionControl executionControl, GenerationRuleExecution generationRuleExecution) {
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
			this.setDecimalFlag(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "DECIMAL_FL"));
			this.setDecimalNumber(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "DECIMAL_NB"));
			this.setDecimalChar(new GenerationRuleParameterExecution(this.getFrameworkExecution(), this.getEoControl(),
					this.getGenerationRuleTypeName(), "DECIMAL_CHR"));

			// Get Parameters
			for (GenerationRuleParameter generationRuleParameter : this.getGenerationRuleExecution().getGenerationRule()
					.getParameters()) {
				if (generationRuleParameter.getName().equalsIgnoreCase("min_value")) {
					this.getMinimumValue().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("max_value")) {
					this.getMaximumValue().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("decimal_fl")) {
					this.getDecimalFlag().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("decimal_nb")) {
					this.getDecimalNumber().setInputValue(generationRuleParameter.getValue());
				} else if (generationRuleParameter.getName().equalsIgnoreCase("decimal_chr")) {
					this.getDecimalChar().setInputValue(generationRuleParameter.getValue());
				}
			}

			// Run the generationRule
			try {
				for (int currentRecord = 0; currentRecord < this.getGenerationRuleExecution().getGenerationExecution()
						.getNumberOfRecords(); currentRecord++) {

					String generatedValue = "";
					if (this.getDecimalFlag().getValue().trim().equalsIgnoreCase("n")) {
						generatedValue = String.valueOf(this.getGenerationRuleExecution().getGenerationExecution()
								.getGenerationRuntime().getGenerationObjectExecution().getNumber()
								.between(Long.parseLong(this.getMinimumValue().getValue()),
										Long.parseLong(this.getMaximumValue().getValue())));
					} else {
						int numberOfDecimals = DEFAULT_DECIMAL_NUMBER;
						if (!this.getDecimalNumber().getValue().trim().equalsIgnoreCase("")) {
							numberOfDecimals = Integer.valueOf(this.getDecimalNumber().getValue());
						}
						
						DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ROOT);
						if (this.getDecimalChar().getValue().trim().equalsIgnoreCase("")) {
							String temp = DEFAULT_DECIMAL_CHAR;
							char charTemp = temp.charAt(0);
							otherSymbols.setDecimalSeparator(charTemp);
						} else {
							String temp = this.getDecimalChar().getValue();
							char charTemp = temp.charAt(0);
							otherSymbols.setDecimalSeparator(charTemp);							
						}
						//otherSymbols.setDecimalSeparator(',');
						//otherSymbols.setGroupingSeparator('.');
						
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("0.");
						for (int i=0; i < numberOfDecimals; i++) {
							stringBuilder.append("0");
						}
						stringBuilder.append("##");
						
						DecimalFormat decimalFormat = new DecimalFormat(stringBuilder.toString(),otherSymbols);



						generatedValue = String.valueOf(decimalFormat.format(this.getGenerationRuleExecution()
								.getGenerationExecution().getGenerationRuntime().getGenerationObjectExecution().getNumber()
								.round(this.getGenerationRuleExecution().getGenerationExecution().getGenerationRuntime()
										.getGenerationObjectExecution().getNumber()
										.between(Double.parseDouble(this.getMinimumValue().getValue()),
												Double.parseDouble(this.getMaximumValue().getValue())),
										numberOfDecimals)));
					}

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

	public GenerationRuleParameterExecution getDecimalFlag() {
		return decimalFlag;
	}

	public void setDecimalFlag(GenerationRuleParameterExecution decimalFlag) {
		this.decimalFlag = decimalFlag;
	}

	public GenerationRuleParameterExecution getDecimalNumber() {
		return decimalNumber;
	}

	public void setDecimalNumber(GenerationRuleParameterExecution decimalNumber) {
		this.decimalNumber = decimalNumber;
	}

	public GenerationRuleParameterExecution getDecimalChar() {
		return decimalChar;
	}

	public void setDecimalChar(GenerationRuleParameterExecution decimalChar) {
		this.decimalChar = decimalChar;
	}

	public static int getDefaultDecimalNumber() {
		return DEFAULT_DECIMAL_NUMBER;
	}

	public static String getDefaultDecimalChar() {
		return DEFAULT_DECIMAL_CHAR;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}