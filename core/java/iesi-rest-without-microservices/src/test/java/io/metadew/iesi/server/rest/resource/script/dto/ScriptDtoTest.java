package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ScriptDtoTest {

    @Test
    public void convertToEntityTest() {
        Script script = new Script(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L),
                "script",
                "description",
                new ScriptVersion(new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L)), "description"),
                Stream.of(new ScriptParameter(new ScriptParameterKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), "name1"), "value1"),
                        new ScriptParameter(new ScriptParameterKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), "name2"), "value2"))
        .collect(Collectors.toList()),
                Stream.of(new Action(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action1")),
                                1L, "action", "action1", "desc1", "comp1", "cond1", "iter1", "Y", "Y", "0",
                                Stream.of(new ActionParameter(new ActionParameterKey(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action1")), "name1"), "value1"),
                                        new ActionParameter(new ActionParameterKey(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action1")), "name2"), "value2"))
                                        .collect(Collectors.toList())),
                        new Action(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action2")),
                                2L, "action", "action2", "desc2", "comp2", "cond2", "iter2", "N", "N", "0",
                                Stream.of(new ActionParameter(new ActionParameterKey(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action2")), "name1"), "value1"),
                                        new ActionParameter(new ActionParameterKey(new ActionKey(new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), IdentifierTools.getActionIdentifier("action2")), "name2"), "value2"))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()),
                Stream.of(new ScriptLabel(new ScriptLabelKey(DigestUtils.sha256Hex(IdentifierTools.getScriptIdentifier("script")+1L+"name1")), new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), "name1", "value1"),
                        new ScriptLabel(new ScriptLabelKey(DigestUtils.sha256Hex(IdentifierTools.getScriptIdentifier("script")+1L+"name2")), new ScriptKey(IdentifierTools.getScriptIdentifier("script"), 1L), "name2", "value2"))
                        .collect(Collectors.toList()));

        ScriptDto scriptDto = new ScriptDto("script","description",
                new ScriptVersionDto(1L, "description"),
                Stream.of(new ScriptParameterDto( "name1", "value1"),
                        new ScriptParameterDto( "name2", "value2"))
                        .collect(Collectors.toList()),
                Stream.of(new ActionDto(1L, "action1", "action", "desc1", "comp1", "cond1", "iter1", true, true, 0,
                        Stream.of(new ActionParameterDto("name1", "value1"),
                                new ActionParameterDto("name2", "value2"))
                                .collect(Collectors.toList())),
                        new ActionDto(2L, "action2", "action", "desc2", "comp2", "cond2", "iter2", false, false, 0,
                        Stream.of(new ActionParameterDto("name1", "value1"),
                                new ActionParameterDto("name2", "value2"))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList()),
                Stream.of(new ScriptLabelDto("name1", "value1"),
                        new ScriptLabelDto("name2", "value2")).collect(Collectors.toList()));
        assertEquals(script, scriptDto.convertToEntity());
    }

}