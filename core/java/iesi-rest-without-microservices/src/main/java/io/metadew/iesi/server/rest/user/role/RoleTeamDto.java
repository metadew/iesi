package io.metadew.iesi.server.rest.user.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
public class RoleTeamDto {

    private final UUID id;
    private final String name;

}
