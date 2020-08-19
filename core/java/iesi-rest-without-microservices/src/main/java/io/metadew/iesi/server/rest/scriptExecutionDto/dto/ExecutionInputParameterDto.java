package io.metadew.iesi.server.rest.scriptExecutionDto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ExecutionInputParameterDto {

    private final String name;
    private final String value;

}
