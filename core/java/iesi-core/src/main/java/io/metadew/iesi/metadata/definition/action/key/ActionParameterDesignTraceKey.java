package io.metadew.iesi.metadata.definition.action.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ActionParameterDesignTraceKey extends MetadataKey {


    private String runId;
    private Long processId;
    private String actionId;
    private String name;

    public ActionParameterDesignTraceKey(String runId, Long processId, String actionId, String name) {
        this.runId = runId;
        this.processId = processId;
        this.actionId = actionId;
        this.name = name;
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }

    public String getActionId() {
        return actionId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        stringBuilder.append(Arrays.stream(fields).map(field -> {
            try {
                return field.getName() + ": " + field.get(this);
            } catch (IllegalAccessException ignored) {
                return field.getName() + ": <private>";
            }
        }).collect(Collectors.joining(", ")));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
