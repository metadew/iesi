package io.metadew.iesi.server.rest.builder;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentParameterDto;
import io.metadew.iesi.server.rest.component.dto.ComponentVersionDto;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentDtoBuilder {
    public static ComponentDto simpleComponentDto(String name, long n) {
        return ComponentDto.builder()
                .type("http.request")
                .name(name)
                .description(name + " desc")
                .version(
                        new ComponentVersionDto(n, name + " " + n)
                )
                .parameters(
                        Stream.of(
                                new ComponentParameterDto("connection", "myconnection"),
                                new ComponentParameterDto("endpoint", "/endpoiint"),
                                new ComponentParameterDto("type", "GET")
                        ).collect(Collectors.toList())
                )
                .attributes(new ArrayList<>())
                .build();
    }
}
