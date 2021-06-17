package io.metadew.iesi.server.rest.connection.dto;

import lombok.*;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectionEnvironmentDto {
    private String environment;
    private Set<ConnectionParameterDto> parameters;
}
