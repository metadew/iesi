//package io.metadew.iesi.metadata.configuration.type;
//
//import io.metadew.iesi.metadata.definition.subroutine.SubroutineType;
//import io.metadew.iesi.metadata.definition.subroutine.SubroutineTypeParameter;
//
//public class SubroutineTypeParameterConfiguration {
//
//    private SubroutineTypeParameter subroutineTypeParameter;
//
//    // Constructors
//    public SubroutineTypeParameterConfiguration(SubroutineTypeParameter subroutineTypeParameter) {
//        this.setSubroutineTypeParameter(subroutineTypeParameter);
//    }
//
//    public SubroutineTypeParameterConfiguration() {
//    }
//
//    public SubroutineTypeParameter getSubroutineTypeParameter(String subroutineTypeName, String subroutineTypeParameterName) {
//        SubroutineTypeParameter subroutineTypeParameterResult = null;
//        SubroutineTypeConfiguration subroutineTypeConfiguration = new SubroutineTypeConfiguration();
//        SubroutineType subroutineType = subroutineTypeConfiguration.getSubroutineType(subroutineTypeName);
//        for (SubroutineTypeParameter subroutineTypeParameter : subroutineType.getParameters()) {
//            if (subroutineTypeParameter.getName().equalsIgnoreCase(subroutineTypeParameterName)) {
//                subroutineTypeParameterResult = subroutineTypeParameter;
//                break;
//            }
//        }
//        return subroutineTypeParameterResult;
//    }
//
//    // Getters and Setters
//    public SubroutineTypeParameter getSubroutineTypeParameter() {
//        return subroutineTypeParameter;
//    }
//
//    public void setSubroutineTypeParameter(SubroutineTypeParameter subroutineTypeParameter) {
//        this.subroutineTypeParameter = subroutineTypeParameter;
//    }
//
//}