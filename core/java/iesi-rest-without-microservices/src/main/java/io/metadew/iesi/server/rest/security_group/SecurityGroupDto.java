package io.metadew.iesi.server.rest.security_group;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityGroupDto {

    private UUID id;
    private String name;
    private Set<SecurityGroupTeamDto> teams;
    private Set<SecurityObjectDto> securityObjects;

}
