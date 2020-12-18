package io.metadew.iesi.metadata.definition.connection.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ConnectionTrace extends Metadata<ConnectionTraceKey> {

    private final String runId;
    private final Long processId;
    private final String actionParameter;
    private final String name;
    private final String type;
    private final String description;

    public ConnectionTrace(ConnectionTraceKey metadataKey, String runId, Long processId, String actionParameter, String name, String type, String description) {
        super(metadataKey);
        this.runId = runId;
        this.processId = processId;
        this.actionParameter = actionParameter;
        this.name = name;
        this.type = type;
        this.description = description;
    }


}
