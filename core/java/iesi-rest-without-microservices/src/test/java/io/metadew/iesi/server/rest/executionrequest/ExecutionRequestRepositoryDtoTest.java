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
import io.metadew.iesi.metadata.repository.MetadataRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
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

    @BeforeAll
    static void initialize() {
        //MetadataRepositoryConfiguration.getInstance().getMetadataRepositories().forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @AfterAll
    static void teardown() {
        //MetadataRepositoryConfiguration.getInstance().getMetadataRepositories().forEach(MetadataRepository::dropAllTables);
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
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);

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
                        "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(1, requestTimestamp, 2,
                        1, "script2", 1L, "PRIVATE",
                        "test", 1, 1);

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
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);

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
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);

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
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);

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
        Map<String, Object> executionRequest1Map = ExecutionRequestBuilder.generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder.generateExecutionRequest(2, requestTimestamp.plus(1L, ChronoUnit.SECONDS), 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script2", 1L, "PUBLIC", "test", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "test", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script2", 1L, "PUBLIC", "test", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "test", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "production", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 1, 1, "script1", 2L, "PUBLIC", "production", 1, 1);

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
                .generateExecutionRequest(1, requestTimestamp, 2, 1, "script1", 1L, "PUBLIC", "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2, 1, "script1", 2L, "PUBLIC", "production", 1, 1);

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
                        "test", 1, 1);
        Map<String, Object> executionRequest2Map = ExecutionRequestBuilder
                .generateExecutionRequest(2, requestTimestamp, 2,
                        1, "script2", 2L, "PRIVATE",
                        "production", 1, 1);

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