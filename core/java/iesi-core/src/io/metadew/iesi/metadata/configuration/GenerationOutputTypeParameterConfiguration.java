package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationOutputTypeParameter;
import io.metadew.iesi.metadata.definition.GenerationOutputType;

public class GenerationOutputTypeParameterConfiguration {

	private GenerationOutputTypeParameter generationOutputTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationOutputTypeParameterConfiguration(GenerationOutputTypeParameter generationOutputTypeParameter, FrameworkExecution processiongTools) {
		this.setgenerationOutputTypeParameter(generationOutputTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public GenerationOutputTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get GenerationOutput Type Parameter
	public GenerationOutputTypeParameter getGenerationOutputTypeParameter(String GenerationOutputTypeName, String GenerationOutputTypeParameterName) {
		GenerationOutputTypeParameter GenerationOutputTypeParameterResult = null;
		GenerationOutputTypeConfiguration GenerationOutputTypeConfiguration = new GenerationOutputTypeConfiguration(this.getFrameworkExecution());
		GenerationOutputType GenerationOutputType = GenerationOutputTypeConfiguration.getGenerationOutputType(GenerationOutputTypeName);
		for (GenerationOutputTypeParameter GenerationOutputTypeParameter : GenerationOutputType.getParameters()) {
			if (GenerationOutputTypeParameter.getName().equalsIgnoreCase(GenerationOutputTypeParameterName)) {
				GenerationOutputTypeParameterResult = GenerationOutputTypeParameter;
				break;
			}
		}
		return GenerationOutputTypeParameterResult;
	}

	// Getters and Setters
	public GenerationOutputTypeParameter getgenerationOutputTypeParameter() {
		return generationOutputTypeParameter;
	}

	public void setgenerationOutputTypeParameter(GenerationOutputTypeParameter generationOutputTypeParameter) {
		this.generationOutputTypeParameter = generationOutputTypeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}