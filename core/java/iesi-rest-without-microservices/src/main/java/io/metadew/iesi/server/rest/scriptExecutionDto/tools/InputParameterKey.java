package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class InputParameterKey {

    private final String runId;
    private final Long processId;
    private final String inputParameterName;

}
