package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.configuration.action.ActionBuilder;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptVersionBuilder {

    private final String scriptId;
    private String securityGroupName;
    private SecurityGroupKey securityGroupKey;
    private final long versionNumber;
    private int numberOfParameters = 0;
    private int numberOfActions = 0;
    private int numberOfLabels = 0;
    private Set<Action> actions = new HashSet<>();
    private Set<ScriptParameter> scriptParameters = new HashSet<>();
    private Set<ScriptLabel> scriptLabels = new HashSet<>();
    private String name;
    private String deletedAt = "NA";

    public ScriptVersionBuilder(String scriptId, long versionNumber) {
        this.scriptId = scriptId;
        this.versionNumber = versionNumber;
    }

    public ScriptVersionBuilder addAction(Action action) {
        this.actions.add(action);
        return this;
    }

    public ScriptVersionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ScriptVersionBuilder securityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
        return this;
    }

    public ScriptVersionBuilder securityGroupKey(SecurityGroupKey securityGroupKey) {
        this.securityGroupKey = securityGroupKey;
        return this;
    }

    public ScriptVersionBuilder addScriptParameter(ScriptParameter scriptParameter) {
        this.scriptParameters.add(scriptParameter);
        return this;
    }

    public ScriptVersionBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }


    public ScriptVersionBuilder numberOfActions(int numberOfActions) {
        this.numberOfActions = numberOfActions;
        return this;
    }

    public ScriptVersionBuilder numberOfLabels(int numberOfLabels) {
        this.numberOfLabels = numberOfLabels;
        return this;
    }

    public ScriptVersionBuilder deleted_At(String deleted_At) {
        this.deletedAt = deleted_At;
        return this;
    }

    public ScriptVersion build() {
        scriptParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new ScriptParameterBuilder(scriptId, versionNumber, "parameter" + i).build())
                .collect(Collectors.toList()));
        actions.addAll(IntStream.range(0, numberOfActions)
                .boxed()
                .map(i -> new ActionBuilder(scriptId, versionNumber, "action" + i).build())
                .collect(Collectors.toList()));
        scriptLabels.addAll(IntStream.range(0, numberOfLabels)
                .boxed()
                .map(i -> new ScriptLabelBuilder(scriptId, versionNumber, "label" + i).build())
                .collect(Collectors.toList()));
        if (securityGroupName == null) {
            securityGroupName = "DEFAULT";
        }
        if (securityGroupKey == null) {
            securityGroupKey = SecurityGroupService.getInstance().get(securityGroupName)
                    .map(SecurityGroup::getMetadataKey)
                    .orElseThrow(() -> new RuntimeException("Could not find Security Group with name" + securityGroupName));
        }
        return new ScriptVersion(
                new ScriptVersionKey(new ScriptKey(scriptId), versionNumber, deletedAt == null ? "NA" : deletedAt),
                new Script(new ScriptKey(scriptId),
                        securityGroupKey,
                        securityGroupName,
                        name == null ? "dummy" : name,
                        "dummy",
                        deletedAt == null ? "NA" : deletedAt),
                "version of script",
                scriptParameters,
                actions,
                scriptLabels,
                "admin",
                "now",
                null,
                null
        );
    }

}
