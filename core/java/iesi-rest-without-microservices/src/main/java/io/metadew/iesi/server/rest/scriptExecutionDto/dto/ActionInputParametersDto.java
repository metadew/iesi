package io.metadew.iesi.server.rest.scriptExecutionDto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActionInputParametersDto {

    private String name;
    private String rawValue;
    private String resolvedValue;

}
