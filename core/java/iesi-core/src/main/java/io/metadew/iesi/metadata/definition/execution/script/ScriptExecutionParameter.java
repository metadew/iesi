//package io.metadew.iesi.metadata.definition.execution.script;
//
//
//import io.metadew.iesi.metadata.definition.Metadata;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionParameterKey;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;
//
//@Data
//@EqualsAndHashCode(callSuper = true)
//@ToString(callSuper = true)
//public class ScriptExecutionParameter extends Metadata<ScriptExecutionParameterKey> {
//
//    private final ScriptExecutionKey scriptExecutionKey;
//    private final String name;
//    private String value;
//
//    public ScriptExecutionParameter(ScriptExecutionParameterKey scriptExecutionParameterKey,
//                                    ScriptExecutionKey scriptExecutionKey, String name, String value) {
//        super(scriptExecutionParameterKey);
//        this.scriptExecutionKey = scriptExecutionKey;
//        this.name = name;
//        this.value = value;
//    }
//
//}