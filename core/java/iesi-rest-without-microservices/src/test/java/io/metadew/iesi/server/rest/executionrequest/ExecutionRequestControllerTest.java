package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestPostDto;
import io.metadew.iesi.server.rest.user.UserDto;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"http", "test"})
@DirtiesContext
class ExecutionRequestControllerTest {

    @Autowired
    private ExecutionRequestController executionRequestController;

    @MockBean
    private ExecutionRequestService executionRequestService;

    @MockBean
    private UserDtoRepository userDtoRepository;

    @MockBean
    private ScriptConfiguration scriptConfiguration;


    @Test
    @WithIesiUser(username = "spring",
            authorities = {"EXECUTION_REQUESTS_WRITE@PUBLIC"})
    void testCreateExecutionRequestsWrite() {
        // Create test method argument(s)
        ExecutionRequestPostDto executionRequestPostDto = ExecutionRequestPostDto.builder()
                .executionRequestLabels(new HashSet<>())
                .requestTimestamp(LocalDateTime.now())
                .scriptExecutionRequests(Stream.of(
                        ScriptExecutionRequestPostDto.builder()
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .build()
                ).collect(Collectors.toSet()))
                .context("context")
                .description("description")
                .email("email")
                .name("name")
                .scope("scope")
                .build();

        // Define mocks behaviour
        UUID userUUID = UUID.randomUUID();
        UserDto userDto = mock(UserDto.class);
        when(userDto.getId())
                .thenReturn(userUUID);
        when(userDto.getUsername())
                .thenReturn("spring");
        when(userDtoRepository.get("spring"))
                .thenReturn(Optional.of(userDto));
        when(scriptConfiguration
                .getSecurityGroup("script1"))
                .thenReturn(Optional.of(new SecurityGroup(
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PUBLIC",
                        new HashSet<>(),
                        new HashSet<>())));

        String newExecutionRequestId = UUID.randomUUID().toString();
        String newScriptExecutionRequestId = UUID.randomUUID().toString();
        LocalDateTime requestTimestamp = LocalDateTime.now();
        AuthenticatedExecutionRequest expectedAuthenticatedExecutionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(newExecutionRequestId))
                .name("name")
                .username("spring")
                .userID(userUUID.toString())
                .context("context")
                .description("description")
                .scope("scope")
                .executionRequestLabels(Stream.of(ExecutionRequestLabel.builder()
                        .metadataKey(new ExecutionRequestLabelKey(UUID.randomUUID().toString()))
                        .executionRequestKey(new ExecutionRequestKey(newExecutionRequestId))
                        .name("key1")
                        .value("value1")
                        .build())
                        .collect(Collectors.toSet()))
                .email("email")
                .scriptExecutionRequests(Stream.of(ScriptNameExecutionRequest.builder()
                        .scriptExecutionRequestKey(new ScriptExecutionRequestKey(newScriptExecutionRequestId))
                        .executionRequestKey(new ExecutionRequestKey(newExecutionRequestId))
                        .scriptName("script1")
                        .scriptVersion(1L)
                        .environment("test")
                        .exit(false)
                        .impersonations(new HashSet<>())
                        .parameters(new HashSet<>())
                        .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                        .build())
                        .collect(Collectors.toList()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .requestTimestamp(requestTimestamp)
                .build();


        when(executionRequestService.createExecutionRequest((ExecutionRequest) argThat(executionRequest -> equalsWithoutUuid((ExecutionRequest) executionRequest, expectedAuthenticatedExecutionRequest))))
                .thenReturn(expectedAuthenticatedExecutionRequest);
        // Perform test method
        ExecutionRequestDto executionRequestDto1 = executionRequestController.post(executionRequestPostDto);

        // Perform assertions
        assertThat(executionRequestDto1)
                .isEqualTo(ExecutionRequestDto.builder()
                        .executionRequestId(newExecutionRequestId)
                        .executionRequestStatus(ExecutionRequestStatus.NEW)
                        .executionRequestLabels(Stream.of(ExecutionRequestLabelDto.builder()
                                .name("key1")
                                .value("value1")
                                .build())
                                .collect(Collectors.toSet()))
                        .context("context")
                        .description("description")
                        .requestTimestamp(requestTimestamp)
                        .email("email")
                        .name("name")
                        .scope("scope")
                        .userId(userUUID.toString())
                        .username("spring")
                        .scriptExecutionRequests(Stream.of(ScriptExecutionRequestDto.builder()
                                .scriptExecutionRequestId(newScriptExecutionRequestId)
                                .executionRequestId(newExecutionRequestId)
                                .scriptName("script1")
                                .scriptVersion(1L)
                                .environment("test")
                                .exit(false)
                                .impersonations(new HashSet<>())
                                .parameters(new HashSet<>())
                                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                .build())
                                .collect(Collectors.toSet()))
                        .build());

    }

    public boolean equalsWithoutUuid(ExecutionRequest executionRequest1, ExecutionRequest executionRequest2) {
        if (!executionRequest1.getClass().equals(executionRequest2.getClass())) {
            return false;
        } else if (!executionRequest1.getName().equals(executionRequest2.getName()) ||
                !executionRequest1.getContext().equals(executionRequest2.getContext()) ||
                !executionRequest1.getEmail().equals(executionRequest2.getEmail()) ||
                !executionRequest1.getScope().equals(executionRequest2.getScope()) ||
                // !executionRequest1.getRequestTimestamp().equals(executionRequest2.getRequestTimestamp()) ||
                !executionRequest1.getExecutionRequestStatus().equals(executionRequest2.getExecutionRequestStatus()) ||
                !executionRequest1.getDescription().equals(executionRequest2.getDescription())) {
            return false;
        } else if (executionRequest1.getExecutionRequestLabels().stream()
                .anyMatch(label1 -> executionRequest2.getExecutionRequestLabels().stream()
                        .noneMatch(label2 -> label2.getValue().equals(label1.getValue())))) {
            return false;
        } else if (executionRequest1.getScriptExecutionRequests().stream()
                .noneMatch(scriptExecutionRequest1 -> executionRequest2.getScriptExecutionRequests().stream()
                        .anyMatch(scriptExecutionRequest2 -> equalsWithoutUuid(scriptExecutionRequest1, scriptExecutionRequest2)))) {
            return false;
        }
        if (executionRequest1 instanceof AuthenticatedExecutionRequest) {
            if (!((AuthenticatedExecutionRequest) executionRequest1).getUsername().equals(((AuthenticatedExecutionRequest) executionRequest2).getUsername()) ||
                    !((AuthenticatedExecutionRequest) executionRequest1).getUserID().equals(((AuthenticatedExecutionRequest) executionRequest2).getUserID())) {
                return false;
            }
        }

        return true;

    }

    public boolean equalsWithoutUuid(ScriptExecutionRequest scriptExecutionRequest1, ScriptExecutionRequest scriptExecutionRequest2) {
        if (!scriptExecutionRequest1.getClass().equals(scriptExecutionRequest2.getClass())) {
            return false;
        } else if (!scriptExecutionRequest1.getScriptExecutionRequestStatus().equals(scriptExecutionRequest2.getScriptExecutionRequestStatus()) ||
                !scriptExecutionRequest1.getEnvironment().equals(scriptExecutionRequest2.getEnvironment())) {
            return false;
        } else if (
                scriptExecutionRequest1.getParameters().size() != scriptExecutionRequest2.getParameters().size() ||
                        scriptExecutionRequest1.getParameters().stream()
                                .anyMatch(requestParameter1 -> scriptExecutionRequest2.getParameters().stream()
                                        .noneMatch(requestParameter2 -> requestParameter2.getName().equals(requestParameter1.getName()) &&
                                                requestParameter1.getValue().equals(requestParameter2.getValue())))) {
            return false;
        } else if (scriptExecutionRequest1.getImpersonations().size() != scriptExecutionRequest2.getImpersonations().size()) {
            return false;
        }
        if (scriptExecutionRequest1 instanceof ScriptNameExecutionRequest) {
            if (!((ScriptNameExecutionRequest) scriptExecutionRequest1).getScriptName().equals(((ScriptNameExecutionRequest) scriptExecutionRequest2).getScriptName()) ||
                    !((ScriptNameExecutionRequest) scriptExecutionRequest1).getScriptVersion().equals(((ScriptNameExecutionRequest) scriptExecutionRequest2).getScriptVersion())) {
                return false;
            }
        }
        return true;
    }

}
