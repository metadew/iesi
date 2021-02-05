package io.metadew.iesi.server.rest.user.team;

import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamSecurityGroupDto {

    private UUID id;
    private String name;

}