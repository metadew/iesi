package io.metadew.iesi.server.rest.user.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserDto {

    private UUID id;
    private String username;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;

}
