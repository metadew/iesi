package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.server.rest.dataset.DatasetDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "user", collectionRelation = "users")
@Builder
public class UserDto extends RepresentationModel<DatasetDto> {

    private UUID id;
    private String username;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private Set<UserRoleDto> roles;

}
