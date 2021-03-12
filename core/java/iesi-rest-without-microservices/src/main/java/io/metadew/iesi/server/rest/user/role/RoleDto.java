package io.metadew.iesi.server.rest.user.role;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

    private UUID id;
    private String name;
    private Set<PrivilegeDto> privileges;
    private Set<RoleUserDto> users;

}
