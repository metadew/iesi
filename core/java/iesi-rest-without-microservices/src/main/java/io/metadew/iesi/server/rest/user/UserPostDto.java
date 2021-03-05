package io.metadew.iesi.server.rest.user;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPostDto {

    private String username;
    private String password;

}
