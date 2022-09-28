package io.metadew.iesi.server.rest.user;

import lombok.*;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPutDto {
    private String username;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private Set<UserRoleDto> roles;
}
