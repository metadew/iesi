package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ActionExecutionKey {
    private final Long prcId;
    private final String actionId;
}
