package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ExecutionRequestServiceTest {

    @Autowired
    private ExecutionRequestService executionRequestService;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @BeforeEach
    void beforeEach() {
        metadataRepositoryConfiguration.clearAllTables();
    }


    @Test
    void createExecutionRequestRightPolicyTest() throws NoSuchFieldException {
        ExecutionRequestConfiguration executionRequestConfiguration = Mockito.mock(ExecutionRequestConfiguration.class);
        ExecutionRequestConfiguration executionRequestConfigurationSpy = Mockito.spy(executionRequestConfiguration);
        ExecutionRequest executionRequest = buildExecutionRequest();

        FieldSetter.setField(executionRequestService, executionRequestService.getClass().getDeclaredField("executionRequestConfiguration"), executionRequestConfigurationSpy);

        Mockito.doNothing().when(executionRequestConfigurationSpy).insert(ArgumentMatchers.any(ExecutionRequest.class));

        assertThatCode(() -> executionRequestService.createExecutionRequest(executionRequest)).doesNotThrowAnyException();
    }

    @Test
    void createExecutionRequestWrongPolicyTest() {
        ExecutionRequest executionRequest = buildExecutionRequest();
        executionRequest.setExecutionRequestLabels(Stream.of(
                new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey("id"),
                        new ExecutionRequestKey("id"),
                        "label_1",
                        "value_1"
                ),
                new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey("id"),
                        new ExecutionRequestKey("id"),
                        "label2",
                        "value_2"
                ),
                new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey("id"),
                        new ExecutionRequestKey("id"),
                        "label_2",
                        "value_2"
                )
        ).collect(Collectors.toSet()));

        assertThatThrownBy(() -> executionRequestService.createExecutionRequest(executionRequest))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("name does not contain the mandatory label \"label_3\" defined in the policy \"policy_2\"");
    }

    @Test
    void createExecutionRequestDisabledPolicyTest() throws NoSuchFieldException {
        ExecutionRequestConfiguration executionRequestConfiguration = Mockito.mock(ExecutionRequestConfiguration.class);
        ExecutionRequestConfiguration executionRequestConfigurationSpy = Mockito.spy(executionRequestConfiguration);
        ExecutionRequest executionRequest = buildExecutionRequest();

        FieldSetter.setField(executionRequestService, executionRequestService.getClass().getDeclaredField("executionRequestConfiguration"), executionRequestConfigurationSpy);

        Mockito.doNothing().when(executionRequestConfigurationSpy).insert(ArgumentMatchers.any(ExecutionRequest.class));

        assertThatCode(() -> executionRequestService.createExecutionRequest(executionRequest)).doesNotThrowAnyException();
    }

    private ExecutionRequest buildExecutionRequest() {
        return new AuthenticatedExecutionRequest(
                new ExecutionRequestKey("id"),
                LocalDateTime.now(),
                "name",
                "description",
                "email",
                "scope",
                "context",
                ExecutionRequestStatus.NEW,
                new ArrayList<>(),
                Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("id"),
                                new ExecutionRequestKey("id"),
                                "label_1",
                                "value_1"
                        ),
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("id"),
                                new ExecutionRequestKey("id"),
                                "label_2",
                                "value_2"
                        ),
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("id"),
                                new ExecutionRequestKey("id"),
                                "label_3",
                                "value_3"
                        ),
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("id"),
                                new ExecutionRequestKey("id"),
                                "label_4",
                                "value_4"
                        )
                ).collect(Collectors.toSet()),
                "userId",
                "username"
        );
    }
}