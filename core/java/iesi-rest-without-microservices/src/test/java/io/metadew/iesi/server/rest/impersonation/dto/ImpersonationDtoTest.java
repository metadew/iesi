package io.metadew.iesi.server.rest.impersonation.dto;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ImpersonationDtoTest {

    @Test
    public void convertToEntityTest() {
        Impersonation impersonation = new Impersonation(new ImpersonationKey("name"),
                "description",
                Stream.of(new ImpersonationParameter(new ImpersonationParameterKey(new ImpersonationKey("name"), "name1"), "impersonated", "value1"),
                        new ImpersonationParameter(new ImpersonationParameterKey(new ImpersonationKey("name"), "name2"), "impersonated", "value2"))
                        .collect(Collectors.toList()));
        ImpersonationDto impersonationDto = new ImpersonationDto("name","description",
                Stream.of(new ImpersonationParameterDto( "name1", "impersonated", "value1"),
                        new ImpersonationParameterDto("name2", "impersonated",  "value2"))
                        .collect(Collectors.toList()));
        assertEquals(impersonation, impersonationDto.convertToEntity());
    }

}