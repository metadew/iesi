package io.metadew.iesi.server.rest.user.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TeamSecurityGroupPutDto {
    public UUID id;
    public String name;
}
