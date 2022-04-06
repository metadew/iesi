package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.script.audit.IScriptDesignAuditService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
public class ScriptServiceTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptService scriptService;

    @BeforeEach
    void beforeEach() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void createScriptRightPolicy() {
        ScriptDesignAuditConfiguration scriptConfiguration = Mockito.mock(ScriptDesignAuditConfiguration.class);
        IScriptDesignAuditService scriptDesignAuditService = Mockito.mock(IScriptDesignAuditService.class);

        SecurityGroupConfiguration.getInstance().insert(new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "PUBLIC", new HashSet<>(), new HashSet<>()));
        ScriptBuilder scriptBuilder = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script"), 1L);
        Script script = scriptBuilder.securityGroupName("PUBLIC").build();

        script.setLabels(Stream.of(
                new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), script.getMetadataKey(), "label_1", "test"),
                new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), script.getMetadataKey(), "label_2", "test")
                ).collect(Collectors.toList())
        );

        Mockito.doNothing().when(scriptDesignAuditService).convertToScriptAudit(ArgumentMatchers.any(), ArgumentMatchers.any());

        assertThatCode(() -> scriptService.createScript(script)).doesNotThrowAnyException();
    }

    @Test
    void createScriptWrongPolicy() {
        SecurityGroupConfiguration.getInstance().insert(new SecurityGroup(new SecurityGroupKey(UUID.randomUUID()), "PUBLIC", new HashSet<>(), new HashSet<>()));
        ScriptBuilder scriptBuilder = new ScriptBuilder(IdentifierTools.getScriptIdentifier("scriptname"), 1L);
        Script script = scriptBuilder.name("scriptname").securityGroupName("PUBLIC").build();

        script.setLabels(Stream.of(
                        new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), script.getMetadataKey(), "label_1", "test"),
                        new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), script.getMetadataKey(), "label2", "test")
                ).collect(Collectors.toList())
        );

        MetadataPolicyConfiguration.getInstance();
        assertThatThrownBy(() -> scriptService.createScript(script))
                .isInstanceOf(PolicyVerificationException.class)
                        .hasMessage("scriptname does not contain mandatory label: label_2");
    }
}
