package io.metadew.iesi.server.rest.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;
    private String username;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private Set<UserRoleDto> roles;

}
