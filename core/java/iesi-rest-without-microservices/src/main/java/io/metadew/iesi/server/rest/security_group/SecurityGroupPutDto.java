package io.metadew.iesi.server.rest.security_group;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityGroupPutDto {

    private UUID id;
    private String name;
    private Set<SecurityGroupTeamPutDto> teams;
    private Set<SecurityObjectPutDto> securityObjects;

}
