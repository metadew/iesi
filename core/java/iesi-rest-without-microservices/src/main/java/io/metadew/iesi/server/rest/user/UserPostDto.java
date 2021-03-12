package io.metadew.iesi.server.rest.user;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
=======
import lombok.*;
>>>>>>> master

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< HEAD
=======
@Builder
>>>>>>> master
public class UserPostDto {

    private String username;
    private String password;
<<<<<<< HEAD
    private List<AuthorityDto> authorities;
=======
>>>>>>> master

}
