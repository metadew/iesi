package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ExecInputParameterDto {

    private final String name;
    private final String value;

}
