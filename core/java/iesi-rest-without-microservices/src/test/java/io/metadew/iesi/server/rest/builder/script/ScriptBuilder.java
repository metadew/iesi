//package io.metadew.iesi.server.rest.builder.script;
//
//import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
//import io.metadew.iesi.metadata.definition.action.Action;
//import io.metadew.iesi.metadata.definition.script.Script;
//import io.metadew.iesi.metadata.definition.script.ScriptLabel;
//import io.metadew.iesi.metadata.definition.script.ScriptParameter;
//import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
//import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
//import io.metadew.iesi.metadata.definition.security.SecurityGroup;
//import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
//import io.metadew.iesi.server.rest.builder.action.ActionBuilder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class ScriptBuilder {
//
//    private final String scriptId;
//    private String securityGroupName;
//    private SecurityGroupKey securityGroupKey;
//    private final long versionNumber;
//    private int numberOfParameters = 0;
//    private int numberOfActions = 0;
//    private int numberOfLabels = 0;
//    private List<Action> actions = new ArrayList<>();
//    private List<ScriptParameter> scriptParameters = new ArrayList<>();
//    private List<ScriptLabel> scriptLabels = new ArrayList<>();
//    private String name;
//    private String deletedAt;
//
//    public ScriptBuilder(String scriptId, long versionNumber) {
//        this.scriptId = scriptId;
//        this.versionNumber = versionNumber;
//    }
//
//    public ScriptBuilder addAction(Action action) {
//        this.actions.add(action);
//        return this;
//    }
//
//    public ScriptBuilder name(String name) {
//        this.name = name;
//        return this;
//    }
//
//    public ScriptBuilder securityGroupName(String securityGroupName) {
//        this.securityGroupName = securityGroupName;
//        return this;
//    }
//
//    public ScriptBuilder securityGroupKey(SecurityGroupKey securityGroupKey) {
//        this.securityGroupKey = securityGroupKey;
//        return this;
//    }
//
//    public ScriptBuilder addScriptParameter(ScriptParameter scriptParameter) {
//        this.scriptParameters.add(scriptParameter);
//        return this;
//    }
//
//    public ScriptBuilder numberOfParameters(int numberOfParameters) {
//        this.numberOfParameters = numberOfParameters;
//        return this;
//    }
//
//
//    public ScriptBuilder numberOfActions(int numberOfActions) {
//        this.numberOfActions = numberOfActions;
//        return this;
//    }
//
//    public ScriptBuilder numberOfLabels(int numberOfLabels) {
//        this.numberOfLabels = numberOfLabels;
//        return this;
//    }
//
//    public ScriptBuilder deletedAt(String deletedAt){
//        this.deletedAt = deletedAt;
//        return this;
//    }
//
//    public Script build() {
//        scriptParameters.addAll(IntStream.range(0, numberOfParameters)
//                .boxed()
//                .map(i -> new ScriptParameterBuilder(scriptId, versionNumber, "parameter" + i).build())
//                .collect(Collectors.toList()));
//        actions.addAll(IntStream.range(0, numberOfActions)
//                .boxed()
//                .map(i -> new ActionBuilder(scriptId, versionNumber,"action" + i).build())
//                .collect(Collectors.toList()));
//        scriptLabels.addAll(IntStream.range(0, numberOfLabels)
//                .boxed()
//                .map(i -> new ScriptLabelBuilder(scriptId, versionNumber, "label" + i).build())
//                .collect(Collectors.toList()));
//        if (securityGroupName == null) {
//            securityGroupName = "DEFAULT";
//        }
//        if (securityGroupKey == null) {
//            securityGroupKey = SecurityGroupConfiguration.getInstance().getByName(securityGroupName)
//                    .map(SecurityGroup::getMetadataKey)
//                    .orElseThrow(() -> new RuntimeException("Could not find Security Group with name" + securityGroupName));
//        }
//        return new Script(new ScriptVersionKey(new ScriptKey(scriptId), versionNumber, "NA")),
//                securityGroupKey,
//                securityGroupName,
//                name == null ? "dummy" : name,
//                "dummy",
//                new ScriptVersionBuilder(scriptId, versionNumber).build(),
//                scriptParameters,
//                actions, scriptLabels);
//    }
//
//}
