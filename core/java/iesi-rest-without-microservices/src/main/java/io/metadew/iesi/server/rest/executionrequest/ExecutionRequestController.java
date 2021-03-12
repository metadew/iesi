package io.metadew.iesi.server.rest.executionrequest;


import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
<<<<<<< HEAD
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
=======
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.service.user.IESIPrivilege;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
>>>>>>> master
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoModelAssembler;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
import java.util.ArrayList;
>>>>>>> master
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@Tag(name = "execution requests", description = "Everything about execution requests")
<<<<<<< HEAD
@RequestMapping("/execution_requests")
=======
@RequestMapping("/execution-requests")
>>>>>>> master
public class ExecutionRequestController {

    private final ExecutionRequestDtoModelAssembler executionRequestDtoModelAssembler;
    private final IExecutionRequestService executionRequestService;
    private final PagedResourcesAssembler<ExecutionRequestDto> executionRequestDtoResourceAssemblerPage;
    private final IesiSecurityChecker iesiSecurityChecker;
    private final ScriptConfiguration scriptConfiguration;

    @Autowired
    ExecutionRequestController(ExecutionRequestService executionRequestService,
                               ExecutionRequestDtoModelAssembler executionRequestDtoModelAssembler,
                               PagedResourcesAssembler<ExecutionRequestDto> executionRequestDtoResourceAssemblerPage, IesiSecurityChecker iesiSecurityChecker, ScriptConfiguration scriptConfiguration) {
        this.executionRequestService = executionRequestService;
        this.executionRequestDtoModelAssembler = executionRequestDtoModelAssembler;
        this.executionRequestDtoResourceAssemblerPage = executionRequestDtoResourceAssemblerPage;
        this.iesiSecurityChecker = iesiSecurityChecker;
        this.scriptConfiguration = scriptConfiguration;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_READ')")
    public PagedModel<ExecutionRequestDto> getAll(Pageable pageable,
                                                  @RequestParam(required = false, name = "script") String script,
                                                  @RequestParam(required = false, name = "version") String version,
                                                  @RequestParam(required = false, name = "environment") String environment,
                                                  @RequestParam(required = false, name = "label") String labelKeyCombination) {
        List<ExecutionRequestFilter> executionRequestFilters = extractScriptFilterOptions(script, version, environment, labelKeyCombination);
        Page<ExecutionRequestDto> executionRequestDtoPage = executionRequestService
                .getAll(SecurityContextHolder.getContext().getAuthentication(), pageable, executionRequestFilters);
        if (executionRequestDtoPage.hasContent())
            return executionRequestDtoResourceAssemblerPage.toModel(executionRequestDtoPage, executionRequestDtoModelAssembler::toModel);
        return (PagedModel<ExecutionRequestDto>) executionRequestDtoResourceAssemblerPage.toEmptyModel(executionRequestDtoPage, ExecutionRequestDto.class);
    }

    private List<ExecutionRequestFilter> extractScriptFilterOptions(String name, String version, String environment, String labelKeyCombination) {
        List<ExecutionRequestFilter> executionRequestFilters = new ArrayList<>();
        if (name != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, name, false));
        }
        if (labelKeyCombination != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, labelKeyCombination, false));
        }
        if (environment != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, environment, false));
        }
        if (version != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, version, true));
        }
        return executionRequestFilters;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_READ')")
    @PostAuthorize("hasPrivilege('EXECUTION_REQUESTS_READ', returnObject.scriptExecutionRequests.![securityGroupName])")
    public ExecutionRequestDto getById(@PathVariable String id) {
<<<<<<< HEAD
        return executionRequestService.getById(id)
                .map(executionRequestDtoResourceAssembler::toModel)
=======
        return executionRequestService
                .getById(null, id)
>>>>>>> master
                .orElseThrow(() -> new MetadataDoesNotExistException(new ExecutionRequestKey(id)));
    }

    @PostMapping("")
<<<<<<< HEAD
    public ExecutionRequestDto post(@RequestBody ExecutionRequestDto executionRequestDto) throws MetadataAlreadyExistsException {
        try {
            ExecutionRequest executionRequest = executionRequestService.createExecutionRequest(executionRequestDto);
            return executionRequestDtoResourceAssembler.toModel(executionRequest);
        } catch (ExecutionRequestBuilderException e) {
            throw new RuntimeException(e);
=======
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public ExecutionRequestDto post(@RequestBody ExecutionRequestPostDto executionRequestPostDto) {
        if (!iesiSecurityChecker.hasPrivilege(
                SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.EXECUTION_REQUESTS_MODIFY.getPrivilege(),
                executionRequestPostDto.getScriptExecutionRequests().stream()
                        .map(scriptExecutionRequestPostDto -> scriptConfiguration
                                .getSecurityGroup(scriptExecutionRequestPostDto.getScriptName())
                                .orElseThrow(() -> new RuntimeException(String.format("Cannot find security group of %s", scriptExecutionRequestPostDto.getScriptName()))))
                        .map(SecurityGroup::getName)
                        .collect(Collectors.toList()))) {
            throw new AccessDeniedException("User is not allowed to delete this execution request");
>>>>>>> master
        }
        ExecutionRequest executionRequest = executionRequestService.createExecutionRequest(executionRequestPostDto.convertToEntity());
        return executionRequestDtoModelAssembler.toModel(executionRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public ResponseEntity<Object> deleteById(@PathVariable String id) {
        ExecutionRequestDto executionRequestDto = executionRequestService
                .getById(null, id)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ExecutionRequestKey(id)));
        if (!iesiSecurityChecker.hasPrivilege(
                SecurityContextHolder.getContext().getAuthentication(),
                IESIPrivilege.EXECUTION_REQUESTS_MODIFY.getPrivilege(),
                executionRequestDto.getScriptExecutionRequests().stream()
                        .map(ScriptExecutionRequestDto::getSecurityGroupName)
                        .collect(Collectors.toList()))) {
            throw new AccessDeniedException("User is not allowed to delete this execution request");
        }
        executionRequestService.deleteById(executionRequestDto.getExecutionRequestId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}