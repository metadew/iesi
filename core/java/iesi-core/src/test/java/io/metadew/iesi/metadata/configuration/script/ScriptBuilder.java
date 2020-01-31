package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.metadata.configuration.action.ActionBuilder;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptBuilder {

    private final String scriptId;
    private final long versionNumber;
    private int numberOfParameters = 0;
    private int numberOfActions = 0;
    private List<Action> actions = new ArrayList<>();
    private List<ScriptParameter> scriptParameters = new ArrayList<>();
    private String name;

    public ScriptBuilder(String scriptId, long versionNumber) {
        this.scriptId = scriptId;
        this.versionNumber = versionNumber;
    }

    public ScriptBuilder addAction(Action action) {
        this.actions.add(action);
        return this;
    }

    public ScriptBuilder name(String name) {
        this.name = name;
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

    public Script build() {
        scriptParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ScriptParameterBuilder(scriptId, versionNumber,"parameter" + i).build())
                .collect(Collectors.toList()));
        actions.addAll(IntStream.range(0, numberOfActions)
                .boxed()
                .map(i -> new ActionBuilder(scriptId, versionNumber,"action" + i).build())
                .collect(Collectors.toList()));
        return new Script(new ScriptKey(scriptId, versionNumber), name == null ? "dummy" : name, "dummy",
                new ScriptVersionBuilder(scriptId, versionNumber).build(),
                scriptParameters,
                actions);
    }

}
