//package io.metadew.iesi.server.rest.builder.action;
//
//import io.metadew.iesi.metadata.definition.action.Action;
//import io.metadew.iesi.metadata.definition.action.ActionParameter;
//import io.metadew.iesi.metadata.definition.action.key.ActionKey;
//import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class ActionBuilder {
//
//    private final String scriptId;
//    private final long scriptVersionNumber;
//    private final String actionId;
//    private int numberOfParameters = 0;
//    private List<ActionParameter> actionParameters;
//    private int number;
//    private String name;
//    private String type;
//    private String description;
//
//    public ActionBuilder(String scriptId, long scriptVersionNumber, String actionId) {
//        this.scriptId = scriptId;
//        this.scriptVersionNumber = scriptVersionNumber;
//        this.actionId = actionId;
//        this.actionParameters = new ArrayList<>();
//        this.number = 0;
//    }
//
//    public ActionBuilder numberOfParameters(int numberOfParameters) {
//        this.numberOfParameters = numberOfParameters;
//        return this;
//    }
//
//    public ActionBuilder number(int number) {
//        this.number = number;
//        return this;
//    }
//
//    public ActionBuilder name(String name) {
//        this.name = name;
//        return this;
//    }
//
//    public ActionBuilder type(String type) {
//        this.type = type;
//        return this;
//    }
//
//    public ActionBuilder description(String description) {
//        this.description = description;
//        return this;
//    }
//
//    public ActionBuilder addActionParameter(ActionParameter actionParameter) {
//        this.actionParameters.add(actionParameter);
//        return this;
//    }
//
//    public ActionBuilder actionParameters(List<ActionParameter> actionParameters) {
//        this.actionParameters.addAll(actionParameters);
//        return this;
//    }
//
//    public Action build() {
//        actionParameters.addAll(IntStream.range(0, numberOfParameters)
//                .boxed()
//                .map(i -> new ActionParameterBuilder(scriptId, scriptVersionNumber, actionId, "parameter" + i).build())
//                .collect(Collectors.toList()));
//        return new Action(new ActionKey(new ScriptKey(scriptId, scriptVersionNumber), actionId), number, type,
//                name == null ? "dummy" : name, description, null, null, null, "N", "N",
//                "0", actionParameters);
//    }
//
//}
