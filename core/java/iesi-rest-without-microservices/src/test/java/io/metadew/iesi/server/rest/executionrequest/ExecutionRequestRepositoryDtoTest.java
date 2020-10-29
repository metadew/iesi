package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class ExecutionRequestRepositoryDtoTest {

    @Autowired
    private ExecutionRequestDtoRepository executionRequestDtoRepository;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    private ScriptExecutionConfiguration scriptExecutionConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
    }

    @Test
    void getAllNoExecutionRequests() {
        assertThat(executionRequestDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>())).isEmpty();
    }

    @Test
    void getAllSingleExecutionRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>())).containsOnly(executionRequestDto);
    }

    @Test
    void getAllSingleExecutionRequestWithRunId() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        UUID runId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                .runId(runId.toString())
                .build();
        scriptExecutionConfiguration.insert(scriptExecution);
        executionRequestConfiguration.insert(executionRequest);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.ACCEPTED)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .runId(runId.toString())
                                .runStatus(ScriptRunStatus.RUNNING)
                                .build())
                        .collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>()))
                .containsOnly(executionRequestDto);
    }

    @Test
    void getAllMultipleExecutionRequests() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>())).containsOnly(executionRequestDto, executionRequestDto2);
    }

    @Test
    void getAllPaginated() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 1), new ArrayList<>()))
                .hasSize(1);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(1, 1), new ArrayList<>()))
                .hasSize(1);
    }

    @Test
    void getAllOrderedByRequestTimestamp() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = requestTimestamp.plus(1L, ChronoUnit.SECONDS);
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        System.out.println(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "REQUEST_TIMESTAMP")), new ArrayList<>()).getContent());
        System.out.println(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "REQUEST_TIMESTAMP")), new ArrayList<>()).getContent());
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "REQUEST_TIMESTAMP")), new ArrayList<>())).containsExactly(executionRequestDto2, executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "REQUEST_TIMESTAMP")), new ArrayList<>())).containsExactly(executionRequestDto, executionRequestDto2);
    }

    @Test
    void getAllOrderedByScriptName() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script2")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script2")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "SCRIPT")), new ArrayList<>())).containsExactly(executionRequestDto2, executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "SCRIPT")), new ArrayList<>())).containsExactly(executionRequestDto, executionRequestDto2);
    }

    @Test
    void getAllOrderedByScriptVersion() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script1")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "VERSION")), new ArrayList<>())).containsExactly(executionRequestDto2, executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "VERSION")), new ArrayList<>())).containsExactly(executionRequestDto, executionRequestDto2);
    }

    @Test
    void getAllFilteredByScriptName() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script2")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script2")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto, executionRequestDto2);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript1", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript2", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto2);
    }

    @Test
    void getAllFilteredByScriptVersion() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, "1", true)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, "2", true)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto2);
    }

    @Test
    void getAllFilteredByEnvironment() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("prod")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("prod")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, "es", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, "pro", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto2);
    }

    @Test
    void getAllFilteredByLabel() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label3")
                                .value("value3")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("prod")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label3")
                                .value("value3")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("prod")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script2")
                                .scriptVersion(2L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, "label2:lue2", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto, executionRequestDto2);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, "label1:lue1", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto);
        assertThat(executionRequestDtoRepository.getAll(PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, "label3:lue3", false)).collect(Collectors.toList())))
                .containsOnly(executionRequestDto2);
    }

    @Test
    void getByIdNoExecutionRequest() {
        assertThat(executionRequestDtoRepository.getById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void getByIdSingleExecutionRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getById(executionRequestId)).hasValue(executionRequestDto);
    }

    @Test
    void getByIdMultipleExecutionRequests() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        UUID executionRequestId = UUID.randomUUID();
        UUID scriptExecutionRequestId = UUID.randomUUID();
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        LocalDateTime requestTimestamp2 = LocalDateTime.now();
        UUID executionRequestId2 = UUID.randomUUID();
        UUID scriptExecutionRequestId2 = UUID.randomUUID();
        ExecutionRequest executionRequest2 = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                .requestTimestamp(requestTimestamp2)
                .context("context")
                .email("email")
                .password("password")
                .user("user")
                .space("space")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabel.builder()
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptNameExecutionRequest.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestKey(new ExecutionRequestKey(executionRequestId2.toString()))
                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonation.builder()
                                                .impersonationKey(new ImpersonationKey("impersonation"))
                                                .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()))
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .build())
                                        .collect(Collectors.toSet()))
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameter.builder()
                                                .name("param1")
                                                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestId2.toString()))
                                                .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()))
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
        executionRequestConfiguration.insert(executionRequest);
        executionRequestConfiguration.insert(executionRequest2);
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        ExecutionRequestDto executionRequestDto2 = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp2)
                .executionRequestId(executionRequestId2.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .executionRequestLabels(Stream.of(
                        ExecutionRequestLabelDto.builder()
                                .name("label1")
                                .value("value1")
                                .build(),
                        ExecutionRequestLabelDto.builder()
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestDto.builder()
                                .environment("test")
                                .exit(false)
                                .executionRequestId(executionRequestId2.toString())
                                .scriptExecutionRequestId(scriptExecutionRequestId2.toString())
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .impersonations(Stream.of(
                                        ScriptExecutionRequestImpersonationDto.builder()
                                                .name("impersonation")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .parameters(Stream.of(
                                        ScriptExecutionRequestParameterDto.builder()
                                                .name("param1")
                                                .value("value1")
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build()

                ).collect(Collectors.toSet()))
                .build();
        assertThat(executionRequestDtoRepository.getById(executionRequestId)).hasValue(executionRequestDto);
        assertThat(executionRequestDtoRepository.getById(executionRequestId2)).hasValue(executionRequestDto2);
    }

}