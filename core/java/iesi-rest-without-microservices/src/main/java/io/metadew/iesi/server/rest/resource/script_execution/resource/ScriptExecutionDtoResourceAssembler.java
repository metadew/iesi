//package io.metadew.iesi.server.rest.resource.script_execution.resource;
//
//import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
//import io.metadew.iesi.server.rest.controller.ScriptExecutionController;
//import io.metadew.iesi.server.rest.resource.script_execution.dto.ScriptExecutionDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
//import org.springframework.stereotype.Component;
//
//@Component
//public  class ScriptExecutionDtoResourceAssembler extends RepresentationModelAssemblerSupport<ScriptExecution, ScriptExecutionDto> {
//
//    @Autowired
//    public ScriptExecutionDtoResourceAssembler() {
//        super(ScriptExecutionController.class, ScriptExecutionDto.class);
//    }
//
//    @Override
//    public ScriptExecutionDto toModel(ScriptExecution scriptExecutionRequest) {
//        return convertToDto(scriptExecutionRequest);
//    }
//
//    private ScriptExecutionDto convertToDto(ScriptExecution scriptExecutionRequest) {
//        return new ScriptExecutionDto(scriptExecutionRequest.getMetadataKey().getId(),
//                scriptExecutionRequest.getScriptExecutionRequestKey().getId(),
//                scriptExecutionRequest.getScriptRunStatus(),
//                scriptExecutionRequest.getRunId(),
//                scriptExecutionRequest.getStartTimestamp(),
//                scriptExecutionRequest.getEndTimestamp());
//    }
//}