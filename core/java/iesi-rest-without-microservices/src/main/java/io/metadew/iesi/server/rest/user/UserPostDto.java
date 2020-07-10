package io.metadew.iesi.server.rest.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserPostDto {

    private String username;
    private String password;
    private List<AuthorityDto> authorities;

}
