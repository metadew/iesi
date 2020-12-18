package io.metadew.iesi.server.rest.user.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PrivilegeDto {

    private final UUID uuid;
    private final String privilege;
}
