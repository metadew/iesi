package io.metadew.iesi.server.rest.user;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
=======
import io.metadew.iesi.server.rest.dataset.DatasetDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;
>>>>>>> master

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< HEAD
public class UserDto {

=======
@Relation(value = "user", collectionRelation = "users")
@Builder
public class UserDto extends RepresentationModel<DatasetDto> {

    private UUID id;
>>>>>>> master
    private String username;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
<<<<<<< HEAD
    private List<AuthorityDto> authorities;
=======
    private Set<UserRoleDto> roles;
>>>>>>> master

}
