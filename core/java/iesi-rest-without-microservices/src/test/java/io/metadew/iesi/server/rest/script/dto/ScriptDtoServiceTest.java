package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.action.ActionBuilder;
import io.metadew.iesi.server.rest.builder.action.ActionParameterBuilder;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.builder.script.ScriptLabelBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class ScriptDtoServiceTest {

    @Autowired
    private IScriptDtoService scriptDtoService;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::dropAllTables);
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void teardown() {
    }

    @Test
    void getAllNoScriptsTest() {
        assertEquals(0, scriptDtoService.getAll().size());
    }

    @Test
    void getAllSimpleTest() {
        ActionParameter actionParameter = new ActionParameterBuilder(IdentifierTools.getScriptIdentifier("script1"), 1, "action1", "parameter1")
                .value("value1")
                .build();
        Action action = new ActionBuilder(IdentifierTools.getScriptIdentifier("script1"), 1, "action1")
                .name("action1")
                .number(0)
                .addActionParameter(actionParameter)
                .build();
        ScriptLabel scriptLabel = new ScriptLabelBuilder(IdentifierTools.getScriptIdentifier("script1"), 1, "label1")
                .value("value1")
                .build();
        Script script11 = new ScriptBuilder(IdentifierTools.getScriptIdentifier("script1"), 1)
                .name("script1")
                .addAction(action)
                .addLabel(scriptLabel)
                .build();
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script11);
        assertEquals(1, scriptDtoService.getAll().size());
        assertEquals(new ScriptDto("script1", "dummy", new ScriptVersionDto(1, "dummy"), new ArrayList<>(),
                        Stream.of(new ActionDto(0, "action1", "fwk.dummy",
                                "dummy", null, null, null, false, false,
                                0, Stream.of(new ActionParameterDto("parameter1", "value1")).collect(Collectors.toList())))
                                .collect(Collectors.toList()),
                        Stream.of(new ScriptLabelDto("label1", "value1"))
                                .collect(Collectors.toList()), null, null),
                scriptDtoService.getAll().get(0));
    }


}
