package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.SubroutineType;
import io.metadew.iesi.metadata.definition.SubroutineTypeParameter;

public class SubroutineTypeParameterConfiguration {

    private SubroutineTypeParameter subroutineTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public SubroutineTypeParameterConfiguration(SubroutineTypeParameter subroutineTypeParameter, FrameworkInstance frameworkInstance) {
        this.setSubroutineTypeParameter(subroutineTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public SubroutineTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public SubroutineTypeParameter getSubroutineTypeParameter(String subroutineTypeName, String subroutineTypeParameterName) {
        SubroutineTypeParameter subroutineTypeParameterResult = null;
        SubroutineTypeConfiguration subroutineTypeConfiguration = new SubroutineTypeConfiguration(this.getFrameworkInstance());
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
    public SubroutineTypeParameter getSubroutineTypeParameter() {
        return subroutineTypeParameter;
    }

    public void setSubroutineTypeParameter(SubroutineTypeParameter subroutineTypeParameter) {
        this.subroutineTypeParameter = subroutineTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}