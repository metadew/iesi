package io.metadew.iesi.metadata.definition.action.performance;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;

import java.time.LocalDateTime;

public class ActionPerformance extends Metadata<ActionPerformanceKey> {

    private String context;
    private String actionId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private Double duration;

    public ActionPerformance(ActionPerformanceKey actionPerformanceKey, String context, String actionId, LocalDateTime startTimestamp, LocalDateTime stopTimestamp, Double duration) {
        super(actionPerformanceKey);
        this.context = context;
        this.actionId = actionId;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = stopTimestamp;
        this.duration = duration;
    }

    public String getContext() {
        return context;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public Double getDuration() {
        return duration;
    }

    public String getActionId() {
        return actionId;
    }

}
