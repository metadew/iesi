package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptLabelPolicy;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptPolicyDefinition;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ExecutionRequestPolicyDefinitionTest {

    @BeforeAll
    static void beforeAll() {
        FrameworkConfiguration.getInstance();
        MetadataPolicyConfiguration.getInstance();
    }

    @Test
    void alignsWithOneDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
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
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel3");


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
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2");


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
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel2");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel3");

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
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
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
    void doesNotAlignWithOnePolicyDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy = new ExecutionRequestLabelPolicy("mylabel");
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
    void doesNotAlignWithMultiplePolicyDefinitionAndOneLabelPolicy() {
        ExecutionRequestLabelPolicy executionRequestLabelPolicy1 = new ExecutionRequestLabelPolicy("mylabel");
        ExecutionRequestLabelPolicy executionRequestLabelPolicy2 = new ExecutionRequestLabelPolicy("mylabel2");

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
}
