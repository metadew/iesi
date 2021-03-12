package io.metadew.iesi.server.rest.security_group;

import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityGroupPostDto {

    private UUID id = UUID.randomUUID();
    private String name;

}
