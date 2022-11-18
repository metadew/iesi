package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ExecutionRequestRepositoryDtoTest {

    @Autowired
    private ExecutionRequestDtoRepository executionRequestDtoRepository;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    private ScriptExecutionConfiguration scriptExecutionConfiguration;

    @Autowired
    private ScriptConfiguration scriptConfiguration;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllNoExecutionRequests() {
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>())).isEmpty();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllSingleExecutionRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));


        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>()))
                .containsOnly((ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"));
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PRIVATE"
            })
    void getAllSingleExecutionWrongSecurityGroupRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2,
                        1, "script1", 1L, "PUBLIC",
                        "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2,
                        1, "script2", 1L, "PRIVATE",
                        "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>()))
                .containsOnly((ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"));
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllSingleExecutionRequestWithRunId() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));

        UUID runId = UUID.randomUUID();

        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest1Map.get("scriptExecutionRequest10UUID").toString()))
                .runId(runId.toString())
                .build();
        scriptExecutionConfiguration.insert(scriptExecution);

        ExecutionRequestDto executionRequestDto = (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto");
        executionRequestDto.getScriptExecutionRequests().iterator().next().setRunId(runId.toString());
        executionRequestDto.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.RUNNING);

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>()))
                .containsOnly(executionRequestDto);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllMultipleExecutionRequests() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), Pageable.unpaged(), new ArrayList<>()))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllPaginated() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 1), new ArrayList<>()))
                .hasSize(1);
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(1, 1), new ArrayList<>()))
                .hasSize(1);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllOrderedByRequestTimestamp() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp.plus(1L, ChronoUnit.SECONDS), 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "REQUEST_TIMESTAMP")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "REQUEST_TIMESTAMP")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllOrderedByScriptName() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script2", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "SCRIPT")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "SCRIPT")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllOrderedByScriptVersion() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));


        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "VERSION")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "VERSION")), new ArrayList<>()))
                .containsExactly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByScriptName() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script2", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript1", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, "ript2", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByScriptVersion() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, "1", true)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, "2", true)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByEnvironment() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, "es", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, "pro", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByLabel() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, "label1:lue1", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, "label0:lue0", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByStatus() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        ScriptExecution scriptExecution1 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest1Map.get("scriptExecutionRequest10UUID").toString()))
                .runId(uuid1)
                .build();
        ScriptExecution scriptExecution2 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.WARNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest2Map.get("scriptExecutionRequest20UUID").toString()))
                .runId(uuid2)
                .build();
        scriptExecutionConfiguration.insert(scriptExecution1);
        scriptExecutionConfiguration.insert(scriptExecution2);

        ExecutionRequestDto executionRequestDto1 = (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto");
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunId(uuid1);
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.SUCCESS);

        ExecutionRequestDto executionRequestDto2 = (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto");
        executionRequestDto2.getScriptExecutionRequests().iterator().next().setRunId(uuid2);
        executionRequestDto2.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.WARNING);

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.STATUS, "SUCCESS", true)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.STATUS, "WARNING", true)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.STATUS, "STOPPED", true)).collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByRunId() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));

        UUID runId = UUID.randomUUID();

        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest1Map.get("scriptExecutionRequest10UUID").toString()))
                .runId(runId.toString())
                .build();
        scriptExecutionConfiguration.insert(scriptExecution);

        ExecutionRequestDto executionRequestDto = (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto");
        executionRequestDto.getScriptExecutionRequests().iterator().next().setRunId(runId.toString());
        executionRequestDto.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.RUNNING);

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 1),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.RUN_ID, runId.toString(), false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByRunIdMultiple() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        String runId1 = "bf8d0482-0372-42c5-ac2a-6f0f93aeaa69";
        String runId2 = "cf5n1298-9999-42c5-ac2a-6f0f93aeaa69";

        ScriptExecution scriptExecution1 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest1Map.get("scriptExecutionRequest10UUID").toString()))
                .runId(runId1)
                .build();
        ScriptExecution scriptExecution2 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest2Map.get("scriptExecutionRequest20UUID").toString()))
                .runId(runId2)
                .build();
        scriptExecutionConfiguration.insert(scriptExecution1);
        scriptExecutionConfiguration.insert(scriptExecution2);

        ExecutionRequestDto executionRequestDto1 = (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto");
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunId(runId1);
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.RUNNING);

        ExecutionRequestDto executionRequestDto2 = (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto");
        executionRequestDto2.getScriptExecutionRequests().iterator().next().setRunId(runId2);
        executionRequestDto2.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.RUNNING);

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.RUN_ID, "42c5-ac2a-6f0f93aeaa69", false)).collect(Collectors.toList())))
                .containsOnly(
                        (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"),
                        (ExecutionRequestDto) executionRequest2Map.get("executionRequestDto")
                );
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByRunIdWithoutScriptExecution() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.RUN_ID, "42c5-ac2a-6f0f93aeaa69", false)).collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByRunIdWithUnknownScriptExecution() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        String runId = UUID.randomUUID().toString();
        ScriptExecution scriptExecution1 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequest1Map.get("scriptExecutionRequest10UUID").toString()))
                .runId(runId)
                .build();
        scriptExecutionConfiguration.insert(scriptExecution1);

        ExecutionRequestDto executionRequestDto1 = (ExecutionRequestDto) executionRequest1Map.get("executionRequestDto");
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunId(runId);
        executionRequestDto1.getScriptExecutionRequests().iterator().next().setRunStatus(ScriptRunStatus.RUNNING);

        assertThat(executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2),
                Stream.of(new ExecutionRequestFilter(ExecutionRequestFilterOption.RUN_ID, "42c5-ac2a-6f0f93aeaa69", false)).collect(Collectors.toList())))
                .isEmpty();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByRequester() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequestMap = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequestMap.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequestMap.get("script"));


        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequestMap.get("scriptExecutionRequest10UUID").toString()))
                .runId(UUID.randomUUID().toString())
                .build();

        scriptExecutionConfiguration.insert(scriptExecution);

        Page<ExecutionRequestDto> executionRequestDtos = executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(
                new ExecutionRequestFilter(ExecutionRequestFilterOption.REQUESTER, "spring", true)).collect(Collectors.toList()));

        assertThat(executionRequestDtos)
                .hasSize(1)
                .element(0)
                .extracting("username")
                .isEqualTo("spring");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByUsernames() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequestMap = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequestMap2 = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script2", 1L, "PUBLIC", "test2", 1, 1, "spring2");


        executionRequestConfiguration.insert((ExecutionRequest) executionRequestMap.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequestMap.get("script"));

        executionRequestConfiguration.insert((ExecutionRequest) executionRequestMap2.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequestMap2.get("script"));


        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequestMap.get("scriptExecutionRequest10UUID").toString()))
                .runId(UUID.randomUUID().toString())
                .build();

        ScriptExecution scriptExecution2 = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequestMap2.get("scriptExecutionRequest10UUID").toString()))
                .runId(UUID.randomUUID().toString())
                .build();

        scriptExecutionConfiguration.insert(scriptExecution);
        scriptExecutionConfiguration.insert(scriptExecution2);

        Page<ExecutionRequestDto> executionRequestDtos = executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(
                new ExecutionRequestFilter(ExecutionRequestFilterOption.REQUESTER, "spring", true)).collect(Collectors.toList()));

        assertThat(executionRequestDtos)
                .hasSize(1)
                .element(0)
                .extracting("username")
                .isEqualTo("spring");
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getAllFilteredByUsernameNotFound() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequestMap = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring2");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequestMap.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequestMap.get("script"));

        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now().plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(executionRequestMap.get("scriptExecutionRequest10UUID").toString()))
                .runId(UUID.randomUUID().toString())
                .build();

        scriptExecutionConfiguration.insert(scriptExecution);

        Page<ExecutionRequestDto> executionRequestDtos = executionRequestDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), PageRequest.of(0, 2), Stream.of(
                new ExecutionRequestFilter(ExecutionRequestFilterOption.REQUESTER, "spring", true)).collect(Collectors.toList()));

        assertThat(executionRequestDtos)
                .isEmpty();
    }


    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getByIdNoExecutionRequest() {
        assertThat(executionRequestDtoRepository.getById(SecurityContextHolder.getContext().getAuthentication(), UUID.randomUUID())).isEmpty();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getByIdSingleExecutionRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getById(SecurityContextHolder.getContext().getAuthentication(), (UUID) executionRequest1Map.get("executionRequestUUID")))
                .hasValue((ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"));
        assertThat(executionRequestDtoRepository.getById(SecurityContextHolder.getContext().getAuthentication(), (UUID) executionRequest2Map.get("executionRequestUUID")))
                .hasValue((ExecutionRequestDto) executionRequest2Map.get("executionRequestDto"));
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {
                    "EXECUTION_REQUESTS_READ@PUBLIC"
            })
    void getByIdSingleExecutionWrongSecurityGroupRequest() {
        LocalDateTime requestTimestamp = LocalDateTime.now();
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2,
                        1, "script1", 1L, "PUBLIC",
                        "test", 1, 1, "spring");
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2,
                        1, "script2", 2L, "PRIVATE",
                        "production", 1, 1, "spring");

        executionRequestConfiguration.insert((ExecutionRequest) executionRequest1Map.get("executionRequest"));
        executionRequestConfiguration.insert((ExecutionRequest) executionRequest2Map.get("executionRequest"));
        scriptConfiguration.insert((Script) executionRequest1Map.get("script"));
        scriptConfiguration.insert((Script) executionRequest2Map.get("script"));

        assertThat(executionRequestDtoRepository.getById(SecurityContextHolder.getContext().getAuthentication(), (UUID) executionRequest1Map.get("executionRequestUUID")))
                .hasValue((ExecutionRequestDto) executionRequest1Map.get("executionRequestDto"));
        assertThat(executionRequestDtoRepository.getById(SecurityContextHolder.getContext().getAuthentication(), (UUID) executionRequest2Map.get("executionRequestUUID")))
                .isEmpty();
    }

}