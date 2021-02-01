package io.metadew.iesi.server.rest.scriptExecutionDto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OutputDto {

    private final String name;
    private final String value;

}
