package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;


import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
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

class ScriptPolicyDefinitionTest {

    @BeforeAll
    static void beforeAll() {
        FrameworkConfiguration.getInstance();
        MetadataPolicyConfiguration.getInstance();
    }

    @Test
    void scriptAlignsWithOneDefinitionAndOneLabelPolicy() {
        ScriptLabelPolicy scriptLabelPolicy = new ScriptLabelPolicy("mylabel");
        ScriptPolicyDefinition scriptPolicyDefinition = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy).collect(Collectors.toList()));
        scriptPolicyDefinition.setName("policy-definition");

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                        new ScriptLabelKey("mylabel"),
                        new ScriptKey("script-id", 0L),
                        "mylabel",
                        "mylabel-value"
                )).collect(Collectors.toList()))
                .build();

        assertThatCode(() -> scriptPolicyDefinition.verify(script)).doesNotThrowAnyException();
    }

    @Test
    void scriptAlignsWithOneDefinitionAndMultipleLabelPolicies() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");
        ScriptLabelPolicy scriptLabelPolicy3 = new ScriptLabelPolicy("mylabel3");

        ScriptPolicyDefinition scriptPolicyDefinition = new ScriptPolicyDefinition(Stream.of(
                scriptLabelPolicy1, scriptLabelPolicy2, scriptLabelPolicy3
        ).collect(Collectors.toList()));
        scriptPolicyDefinition.setName("policy-definition");

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel2",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel3"),
                                new ScriptKey("script-id", 0L),
                                "mylabel3",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel3"),
                                new ScriptKey("script-id", 0L),
                                "mylabel4",
                                "mylabel-value")

                ).collect(Collectors.toList()))
                .build();

        assertThatCode(() -> scriptPolicyDefinition.verify(script)).doesNotThrowAnyException();
    }

    @Test
    void scriptAlignsWithMultipleDefinitionAndOneLabelPolicy() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");

        ScriptPolicyDefinition scriptPolicyDefinition1 = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy1).collect(Collectors.toList()));
        ScriptPolicyDefinition scriptPolicyDefinition2 = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy2).collect(Collectors.toList()));

        scriptPolicyDefinition1.setName("policy-definition");
        scriptPolicyDefinition2.setName("policy-definition2");

        List<ScriptPolicyDefinition> scriptPolicyDefinitions = Stream.of(scriptPolicyDefinition1, scriptPolicyDefinition2).collect(Collectors.toList());

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel2",
                                "mylabel-value")
                ).collect(Collectors.toList()))
                .build();

        assertThatCode(() -> {
            scriptPolicyDefinitions.forEach(scriptPolicyDefinition -> {
                scriptPolicyDefinition.verify(script);
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void scriptAlignsWithMultipleDefinitionsAndMultipleLabelPolicies() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");
        ScriptLabelPolicy scriptLabelPolicy3 = new ScriptLabelPolicy("mylabel3");

        ScriptPolicyDefinition scriptPolicyDefinition1 = new ScriptPolicyDefinition(
                Stream.of(scriptLabelPolicy1, scriptLabelPolicy2).collect(Collectors.toList()));
        ScriptPolicyDefinition scriptPolicyDefinition2 = new ScriptPolicyDefinition(
                Stream.of(scriptLabelPolicy3).collect(Collectors.toList()));

        scriptPolicyDefinition1.setName("policy-definition");
        scriptPolicyDefinition2.setName("policy-definition2");

        List<ScriptPolicyDefinition> scriptPolicyDefinitions = Stream.of(scriptPolicyDefinition1, scriptPolicyDefinition2).collect(Collectors.toList());

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel2",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel3"),
                                new ScriptKey("script-id", 0L),
                                "mylabel3",
                                "mylabel-value")
                ).collect(Collectors.toList()))
                .build();

        assertThatCode(() -> {
            scriptPolicyDefinitions.forEach(scriptPolicyDefinition -> {
                scriptPolicyDefinition.verify(script);
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void scriptDoesNotAlignWithOnePolicyDefinitionAndOneLabelPolicy() {
        ScriptLabelPolicy scriptLabelPolicy = new ScriptLabelPolicy("mylabel");
        ScriptPolicyDefinition scriptPolicyDefinition = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy).collect(Collectors.toList()));
        scriptPolicyDefinition.setName("policy-definition");

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                        new ScriptLabelKey("mylabel"),
                        new ScriptKey("script-id", 0L),
                        "label",
                        "mylabel-value"
                )).collect(Collectors.toList()))
                .build();

        assertThatThrownBy(() -> scriptPolicyDefinition.verify(script))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("script does not contain the mandatory label \"mylabel\" defined in the policy \"policy-definition\"");
    }

    @Test
    void scriptDoesNotAlignWithOneDefinitionAndMultipleLabelPolicies() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");
        ScriptLabelPolicy scriptLabelPolicy3 = new ScriptLabelPolicy("mylabel3");

        ScriptPolicyDefinition scriptPolicyDefinition = new ScriptPolicyDefinition(Stream.of(
                scriptLabelPolicy1, scriptLabelPolicy2, scriptLabelPolicy3
        ).collect(Collectors.toList()));
        scriptPolicyDefinition.setName("policy-definition");
        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel3"),
                                new ScriptKey("script-id", 0L),
                                "mylabel3",
                                "mylabel-value")

                ).collect(Collectors.toList()))
                .build();

        assertThatThrownBy(() -> scriptPolicyDefinition.verify(script))
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("script does not contain the mandatory label \"mylabel2\" defined in the policy \"policy-definition\"");
    }

    @Test
    void scriptDoesNotAlignsWithMultipleDefinitionsAndOneLabelPolicy() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");

        ScriptPolicyDefinition scriptPolicyDefinition1 = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy1).collect(Collectors.toList()));
        ScriptPolicyDefinition scriptPolicyDefinition2 = new ScriptPolicyDefinition(Stream.of(scriptLabelPolicy2).collect(Collectors.toList()));

        scriptPolicyDefinition1.setName("policy-definition");
        scriptPolicyDefinition2.setName("policy-definition2");

        List<ScriptPolicyDefinition> scriptPolicyDefinitions = Stream.of(scriptPolicyDefinition1, scriptPolicyDefinition2).collect(Collectors.toList());

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value")
                ).collect(Collectors.toList()))
                .build();

        assertThatThrownBy(() -> {
            scriptPolicyDefinitions.forEach(scriptPolicyDefinition -> {
                scriptPolicyDefinition.verify(script);
            });
        })
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("script does not contain the mandatory label \"mylabel2\" defined in the policy \"policy-definition2\"");
    }

    @Test
    void scriptDoesNotAlignsWithMultipleMultipleDefinitionsAndMultipleLabelPolicies() {
        ScriptLabelPolicy scriptLabelPolicy1 = new ScriptLabelPolicy("mylabel");
        ScriptLabelPolicy scriptLabelPolicy2 = new ScriptLabelPolicy("mylabel2");
        ScriptLabelPolicy scriptLabelPolicy3 = new ScriptLabelPolicy("mylabel3");

        ScriptPolicyDefinition scriptPolicyDefinition1 = new ScriptPolicyDefinition(
                Stream.of(scriptLabelPolicy1, scriptLabelPolicy2).collect(Collectors.toList()));
        ScriptPolicyDefinition scriptPolicyDefinition2 = new ScriptPolicyDefinition(
                Stream.of(scriptLabelPolicy3).collect(Collectors.toList()));

        scriptPolicyDefinition1.setName("policy-definition");
        scriptPolicyDefinition2.setName("policy-definition2");

        List<ScriptPolicyDefinition> scriptPolicyDefinitions = Stream.of(scriptPolicyDefinition1, scriptPolicyDefinition2).collect(Collectors.toList());

        Script script = Script.builder()
                .name("script")
                .labels(Stream.of(new ScriptLabel(
                                new ScriptLabelKey("mylabel"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel2"),
                                new ScriptKey("script-id", 0L),
                                "mylabel2",
                                "mylabel-value"),
                        new ScriptLabel(
                                new ScriptLabelKey("mylabel3"),
                                new ScriptKey("script-id", 0L),
                                "mylabel",
                                "mylabel-value")
                ).collect(Collectors.toList()))
                .build();

        assertThatThrownBy(() -> {
            scriptPolicyDefinitions.forEach(scriptPolicyDefinition -> {
                scriptPolicyDefinition.verify(script);
            });
        })
                .isInstanceOf(PolicyVerificationException.class)
                .hasMessage("script does not contain the mandatory label \"mylabel3\" defined in the policy \"policy-definition2\"");
    }
}
