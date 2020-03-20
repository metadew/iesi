//package io.metadew.iesi.metadata.configuration.type;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.metadew.iesi.metadata.definition.script.type.ScriptType;
//import io.metadew.iesi.metadata.operation.DataObjectOperation;
//import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;
//
//public class ScriptTypeConfiguration {
//
//    private ScriptType scriptType;
//    private String dataObjectType = "ScriptType";
//
//    // Constructors
//    public ScriptTypeConfiguration(ScriptType scriptType) {
//        this.setScriptType(scriptType);
//    }
//
//    public ScriptTypeConfiguration() {
//    }
//
//    // Methods
//    public ScriptType getScriptType(String scriptTypeName) {
//        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), scriptTypeName);
//        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
//        ObjectMapper objectMapper = new ObjectMapper();
//        ScriptType scriptType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
//                ScriptType.class);
//        return scriptType;
//    }
//
//    // Getters and Setters
//    public ScriptType getScriptType() {
//        return scriptType;
//    }
//
//    public void setScriptType(ScriptType scriptType) {
//        this.scriptType = scriptType;
//    }
//
//    public String getDataObjectType() {
//        return dataObjectType;
//    }
//
//    public void setDataObjectType(String dataObjectType) {
//        this.dataObjectType = dataObjectType;
//    }
//
//}