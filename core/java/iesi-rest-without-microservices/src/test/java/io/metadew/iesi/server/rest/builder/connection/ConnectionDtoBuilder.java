package io.metadew.iesi.server.rest.builder.connection;

import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionEnvironmentDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionParameterDto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionDtoBuilder {
    public static ConnectionDto simpleConnectionDto(String name) {
        return ConnectionDto.builder()
                .type("http.request")
                .name(name)
                .description(name + " desc")
                .environments(
                        Stream.of(
                                new ConnectionEnvironmentDto(
                                        "env1",
                                        Stream.of(
                                                new ConnectionParameterDto("host", "host.com"),
                                                new ConnectionParameterDto("port", "8080"),
                                                new ConnectionParameterDto("baseUrl", "/baseUrl"),
                                                new ConnectionParameterDto("TLS", "N")
                                        ).collect(Collectors.toSet())
                                ),
                                new ConnectionEnvironmentDto(
                                        "env2",
                                        Stream.of(
                                                new ConnectionParameterDto("host", "host.com"),
                                                new ConnectionParameterDto("baseUrl", "/baseUrl/v2"),
                                                new ConnectionParameterDto("TLS", "Y")
                                        ).collect(Collectors.toSet())
                                )
                        ).collect(Collectors.toSet())
                )
                .build();
    }
}
