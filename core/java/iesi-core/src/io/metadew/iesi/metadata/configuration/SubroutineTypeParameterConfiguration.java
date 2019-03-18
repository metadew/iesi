package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.SubroutineType;
import io.metadew.iesi.metadata.definition.SubroutineTypeParameter;

public class SubroutineTypeParameterConfiguration {

	private SubroutineTypeParameter subroutineTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public SubroutineTypeParameterConfiguration(SubroutineTypeParameter subroutineTypeParameter, FrameworkExecution frameworkExecution) {
		this.setSubroutineTypeParameter(subroutineTypeParameter);
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public SubroutineTypeParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public SubroutineTypeParameter getSubroutineTypeParameter(String subroutineTypeName, String subroutineTypeParameterName) {
		SubroutineTypeParameter subroutineTypeParameterResult = null;
		SubroutineTypeConfiguration subroutineTypeConfiguration = new SubroutineTypeConfiguration(this.getFrameworkExecution());
		SubroutineType subroutineType = subroutineTypeConfiguration.getSubroutineType(subroutineTypeName);
		for (SubroutineTypeParameter subroutineTypeParameter : subroutineType.getParameters()) {
			if (subroutineTypeParameter.getName().equalsIgnoreCase(subroutineTypeParameterName)) {
				subroutineTypeParameterResult = subroutineTypeParameter;
				break;
			}
		}
		return subroutineTypeParameterResult;
	}
	
	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public SubroutineTypeParameter getSubroutineTypeParameter() {
		return subroutineTypeParameter;
	}

	public void setSubroutineTypeParameter(SubroutineTypeParameter subroutineTypeParameter) {
		this.subroutineTypeParameter = subroutineTypeParameter;
	}

}