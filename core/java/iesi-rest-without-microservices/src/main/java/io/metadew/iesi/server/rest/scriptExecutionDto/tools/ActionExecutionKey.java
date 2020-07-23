package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ActionExecutionKey {
    private final String actionId;
    private final Long prcId;
}
