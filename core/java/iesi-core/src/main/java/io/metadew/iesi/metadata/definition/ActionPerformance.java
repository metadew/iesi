package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.definition.key.ActionPerformanceKey;

import java.time.LocalDateTime;

public class ActionPerformance extends Metadata<ActionPerformanceKey> {

    private ActionPerformanceKey actionPerformanceKey;

    private String context;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private Double duration;

    public ActionPerformance(ActionPerformanceKey actionPerformanceKey, String context, LocalDateTime startTimestamp, LocalDateTime stopTimestamp, Double duration) {
        super(actionPerformanceKey);
        this.actionPerformanceKey = actionPerformanceKey;
        this.context = context;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = stopTimestamp;
        this.duration = duration;
    }

    public ActionPerformanceKey getActionPerformanceKey() {
        return actionPerformanceKey;
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
}
