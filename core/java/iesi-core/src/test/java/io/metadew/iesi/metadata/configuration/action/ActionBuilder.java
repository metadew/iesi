package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionBuilder {

    private final String scriptId;
    private final long scriptVersionNumber;
    private final String actionId;
    private int numberOfParameters = 0;

    public ActionBuilder(String scriptId, long scriptVersionNumber, String actionId) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
        this.actionId = actionId;
    }

    public ActionBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }

    public Action build() {
        List<ActionParameter> actionParameters = IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ActionParameterBuilder(scriptId, scriptVersionNumber, actionId, "parameter" + i).build())
                .collect(Collectors.toList());
        return new Action(new ActionKey(new ScriptVersionKey(new ScriptKey(scriptId), scriptVersionNumber, "NA"), actionId), 1, "fwk.dummy",
                "dummy", "dummy", "", "", "", "", "",
                "0", actionParameters);
    }

}
