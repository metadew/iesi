package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = {Configuration.class, SpringContext.class, FrameworkConfiguration.class, MetadataPolicyConfiguration.class })
public class ExecutionRequestPolicyDefinitionTest {

    @Test
    void alignsWithOneDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition = new ExecutionRequestPolicyDefinition(Stream.of(executionRequestLabelPolicy).collect(Collectors.toList()));
        executionRequestPolicyDefinition.setName("policy-definition");

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("script")
                .executionRequestLabels(Stream.of(new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey("mylabel"),
                        new ExecutionRequestKey("execution-request-id"),
                        "mylabel",
                        "mylabel-value"
                )).collect(Collectors.toSet()))
                .build();

        assertThatCode(() -> executionRequestPolicyDefinition.verify(executionRequest)).doesNotThrowAnyException();
    }

    @Test
    void alignsWithOneDefinitionAndMultipleLabelPolicies() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel3", false);


        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy, executionRequestLabelPolicy1, executionRequestLabelPolicy2
        ).collect(Collectors.toList()));
        executionRequestPolicyDefinition.setName("policy-definition");

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("script")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel2"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel2",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatCode(() -> executionRequestPolicyDefinition.verify(executionRequest)).doesNotThrowAnyException();
    }

    @Test
    void alignsWithMultipleDefinitionsAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2", false);


        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition1 = new ExecutionRequestPolicyDefinition(Stream.of(executionRequestLabelPolicy).collect(Collectors.toList()));
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition2 = new ExecutionRequestPolicyDefinition(Stream.of(executionRequestLabelPolicy1).collect(Collectors.toList()));

        executionRequestPolicyDefinition1.setName("policy-definition");
        executionRequestPolicyDefinition2.setName("policy-definition2");

        List<ExecutionRequestPolicyDefinition> executionRequestPolicyDefinitions = Stream.of(
                executionRequestPolicyDefinition1, executionRequestPolicyDefinition2
        ).collect(Collectors.toList());

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("script")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel2"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel2",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatCode(() -> {
            executionRequestPolicyDefinitions.forEach(executionRequestPolicyDefinition -> {
                executionRequestPolicyDefinition.verify(executionRequest);
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void alignsWithMultipleDefinitionsAndMultipleLabelPolicies() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel3", false);

        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition1 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy, executionRequestLabelPolicy1).collect(Collectors.toList()));
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition2 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy2).collect(Collectors.toList()));

        executionRequestPolicyDefinition1.setName("policy-definition");
        executionRequestPolicyDefinition2.setName("policy-definition2");

        List<ExecutionRequestPolicyDefinition> executionRequestPolicyDefinitions = Stream.of(
                executionRequestPolicyDefinition1, executionRequestPolicyDefinition2
        ).collect(Collectors.toList());

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("script")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel2"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel2",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatCode(() -> {
            executionRequestPolicyDefinitions.forEach(executionRequestPolicyDefinition -> {
                executionRequestPolicyDefinition.verify(executionRequest);
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void doesNotAlignWithOnePolicyDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy).collect(Collectors.toList()));
        executionRequestPolicyDefinition.setName("policy-definition");


        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("executionRequest")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel2"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel2",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatThrownBy(() -> executionRequestPolicyDefinition.verify(executionRequest))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("executionRequest does not contain the mandatory label \"mylabel\" defined in the policy \"policy-definition\"");
    }

    @Test
    void doesNotAlignWithOnePolicyDefinitionAndMultipleLabelPolicies() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel2", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy3 = new ExecutionRequestLabelPolicy("mylabel3", false);

        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy1, executionRequestLabelPolicy2, executionRequestLabelPolicy3).collect(Collectors.toList()));
        executionRequestPolicyDefinition.setName("policy-definition");


        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("executionRequest")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel2"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel2",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatThrownBy(() -> executionRequestPolicyDefinition.verify(executionRequest))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("executionRequest does not contain the mandatory label \"mylabel\" defined in the policy \"policy-definition\"");
    }

    @Test
    void doesNotAlignWithMultiplePolicyDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel2", false);

        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition1 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy1).collect(Collectors.toList()));
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition2 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy2).collect(Collectors.toList()));

        executionRequestPolicyDefinition1.setName("policy-definition");
        executionRequestPolicyDefinition2.setName("policy-definition2");

        List<ExecutionRequestPolicyDefinition> executionRequestPolicyDefinitions = Stream.of(
                executionRequestPolicyDefinition1, executionRequestPolicyDefinition2
        ).collect(Collectors.toList());

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("executionRequest")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatThrownBy(() -> executionRequestPolicyDefinitions.forEach(executionRequestPolicyDefinition -> {
            executionRequestPolicyDefinition.verify(executionRequest);
        }))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("executionRequest does not contain the mandatory label \"mylabel2\" defined in the policy \"policy-definition2\"");
    }

    @Test
    void doesNotAlignWithMultiplePolicyDefinitionAndMultipleLabelPolicies() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel2", false);
        ExecutionRequestLabelPolicy executionRequestLabelPolicy3 = new ExecutionRequestLabelPolicy("mylabel3", false);

        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition1 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy1, executionRequestLabelPolicy2).collect(Collectors.toList()));
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition2 = new ExecutionRequestPolicyDefinition(Stream.of(
                executionRequestLabelPolicy3).collect(Collectors.toList()));

        executionRequestPolicyDefinition1.setName("policy-definition");
        executionRequestPolicyDefinition2.setName("policy-definition2");

        List<ExecutionRequestPolicyDefinition> executionRequestPolicyDefinitions = Stream.of(
                executionRequestPolicyDefinition1, executionRequestPolicyDefinition2
        ).collect(Collectors.toList());

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("executionRequest")
                .executionRequestLabels(Stream.of(
                        new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel3"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel3",
                                "mylabel-value"
                        ), new ExecutionRequestLabel(
                                new ExecutionRequestLabelKey("mylabel4"),
                                new ExecutionRequestKey("execution-request-id"),
                                "mylabel4",
                                "mylabel-value"
                        )).collect(Collectors.toSet()))
                .build();

        assertThatThrownBy(() -> executionRequestPolicyDefinitions.forEach(executionRequestPolicyDefinition -> {
            executionRequestPolicyDefinition.verify(executionRequest);
        }))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("executionRequest does not contain the mandatory label \"mylabel2\" defined in the policy \"policy-definition\"");
    }

    @Test
    void alignsWithDisabledLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel", true);
        ExecutionRequestPolicyDefinition executionRequestPolicyDefinition = new ExecutionRequestPolicyDefinition(Stream.of(executionRequestLabelPolicy).collect(Collectors.toList()));
        executionRequestPolicyDefinition.setName("policy-definition");

        ExecutionRequest executionRequest = NonAuthenticatedExecutionRequest.builder()
                .name("script")
                .executionRequestLabels(new HashSet<>())
                .build();

        assertThatCode(() -> executionRequestPolicyDefinition.verify(executionRequest)).doesNotThrowAnyException();
    }
}
