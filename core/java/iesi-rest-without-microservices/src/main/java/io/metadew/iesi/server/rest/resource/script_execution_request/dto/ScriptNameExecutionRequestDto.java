//package io.metadew.iesi.server.rest.resource.script_execution_request.dto;
//
//import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
//import lombok.*;
//
//import java.util.List;
//import java.util.Map;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode(callSuper = false)
//public class ScriptNameExecutionRequestDto extends ScriptExecutionRequestDto {
//
//    private String scriptName;
//    private Long scriptVersion;
//
//    public ScriptNameExecutionRequestDto(String scriptExecutionRequestId, String executionRequestId, List<Long> actionSelect,
//                                         boolean exit, String impersonation, String environment, Map<String, String> impersonations,
//                                         Map<String, String> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus,
//                                         String scriptName, Long scriptVersion) {
//        super(scriptExecutionRequestId, executionRequestId, actionSelect, exit, impersonation, environment, impersonations, parameters, scriptExecutionRequestStatus);
//        this.scriptName = scriptName;
//        this.scriptVersion = scriptVersion;
//    }
//}
