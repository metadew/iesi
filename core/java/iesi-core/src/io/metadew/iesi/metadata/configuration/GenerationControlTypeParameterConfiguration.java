package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlTypeParameter;
import io.metadew.iesi.metadata.definition.GenerationControlType;

public class GenerationControlTypeParameterConfiguration {

	private GenerationControlTypeParameter generationControlTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public GenerationControlTypeParameterConfiguration(GenerationControlTypeParameter generationControlTypeParameter, FrameworkExecution processiongTools) {
		this.setgenerationControlTypeParameter(generationControlTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public GenerationControlTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get GenerationControl Type Parameter
	public GenerationControlTypeParameter getGenerationControlTypeParameter(String GenerationControlTypeName, String GenerationControlTypeParameterName) {
		GenerationControlTypeParameter GenerationControlTypeParameterResult = null;
		GenerationControlTypeConfiguration GenerationControlTypeConfiguration = new GenerationControlTypeConfiguration(this.getFrameworkExecution());
		GenerationControlType GenerationControlType = GenerationControlTypeConfiguration.getGenerationControlType(GenerationControlTypeName);
		for (GenerationControlTypeParameter GenerationControlTypeParameter : GenerationControlType.getParameters()) {
			if (GenerationControlTypeParameter.getName().equalsIgnoreCase(GenerationControlTypeParameterName)) {
				GenerationControlTypeParameterResult = GenerationControlTypeParameter;
				break;
			}
		}
		return GenerationControlTypeParameterResult;
	}

	// Getters and Setters
	public GenerationControlTypeParameter getgenerationControlTypeParameter() {
		return generationControlTypeParameter;
	}

	public void setgenerationControlTypeParameter(GenerationControlTypeParameter generationControlTypeParameter) {
		this.generationControlTypeParameter = generationControlTypeParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}