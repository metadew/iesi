package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingPair {
    private String iesiName;
    private String adName;
}
