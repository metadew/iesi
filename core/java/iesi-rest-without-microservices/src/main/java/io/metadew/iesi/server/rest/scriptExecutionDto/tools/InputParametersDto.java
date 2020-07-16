package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InputParametersDto {

    private String name;
    private String rawValue;
    private String resolvedValue;

}
