package io.metadew.iesi.server.rest.builder.script;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.builder.action.ActionBuilder;
import io.metadew.iesi.server.rest.builder.action.ActionParameterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptBuilder {

    private final String scriptId;
    private final long versionNumber;
    private int numberOfParameters = 0;
    private int numberOfActions = 0;
    private int numberOfLabels = 0;
    private List<Action> actions = new ArrayList<>();
    private List<ScriptParameter> scriptParameters = new ArrayList<>();
    private List<ScriptLabel> scriptLabels = new ArrayList<>();
    private String name;
    private String description;

    public ScriptBuilder(String scriptId, long versionNumber) {
        this.scriptId = scriptId;
        this.versionNumber = versionNumber;
    }

    public ScriptBuilder addAction(Action action) {
        this.actions.add(action);
        return this;
    }

    public ScriptBuilder addLabel(ScriptLabel label) {
        this.scriptLabels.add(label);
        return this;
    }

    public ScriptBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ScriptBuilder description(String name) {
        this.description = name;
        return this;
    }

    public ScriptBuilder addScriptParameter(ScriptParameter scriptParameter) {
        this.scriptParameters.add(scriptParameter);
        return this;
    }

    public ScriptBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }


    public ScriptBuilder numberOfActions(int numberOfActions) {
        this.numberOfActions = numberOfActions;
        return this;
    }

    public ScriptBuilder numberOfLabels(int numberOfLabels) {
        this.numberOfLabels = numberOfLabels;
        return this;
    }

    public Script build() {
        scriptParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ScriptParameterBuilder(scriptId, versionNumber,"parameter" + i).build())
                .collect(Collectors.toList()));
        actions.addAll(IntStream.range(0, numberOfActions)
                .boxed()
                .map(i -> new ActionBuilder(scriptId, versionNumber,"action" + i).build())
                .collect(Collectors.toList()));
        scriptLabels.addAll(IntStream.range(0, numberOfLabels)
                .boxed()
                .map(i -> new ScriptLabelBuilder(scriptId, versionNumber,"label" + i).build())
                .collect(Collectors.toList()));
        return new Script(new ScriptKey(scriptId, versionNumber), name == null ? "dummy" : name,
                description == null ? "dummy" : description,
                new ScriptVersionBuilder(scriptId, versionNumber).build(),
                scriptParameters,
                actions, scriptLabels);
    }

    public static Script simpleScript(String scriptName, long version, int actionCount, int actionParameterCount, int labelCount) {
        List<Action> actions = IntStream.range(0, actionCount)
                .boxed()
                .map(i -> new ActionBuilder(IdentifierTools.getScriptIdentifier(scriptName), version,"action" + i)
                        .name("action" + i)
                        .number(i)
                        .type("fwk.dummy")
                        .description("dummy action")
                        .actionParameters(IntStream.range(0, actionParameterCount)
                                .boxed()
                                .map(j -> new ActionParameterBuilder(IdentifierTools.getScriptIdentifier(scriptName), version, "action" + i, "parameter" + j)
                                        .value("value" + j)
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        List<ScriptLabel> scriptLabels = IntStream.range(0, labelCount)
                .boxed()
                .map(i -> new ScriptLabelBuilder(IdentifierTools.getScriptIdentifier(scriptName), version,"label" + i)
                        .value("value" + i)
                        .build())
                .collect(Collectors.toList());

        return new Script(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), version),
                scriptName,
                "dummy script",
                new ScriptVersionBuilder(IdentifierTools.getScriptIdentifier(scriptName), version)
                        .description("dummy version")
                        .build(),
                new ArrayList<>(),
                actions,
                scriptLabels);
    }

}
