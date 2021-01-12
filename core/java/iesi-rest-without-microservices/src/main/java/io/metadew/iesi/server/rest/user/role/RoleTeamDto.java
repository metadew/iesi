package io.metadew.iesi.server.rest.user.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RoleTeamDto {

    private final UUID id;
    private final String name;

}
