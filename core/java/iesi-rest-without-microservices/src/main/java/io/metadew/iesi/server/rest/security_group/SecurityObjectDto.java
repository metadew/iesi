package io.metadew.iesi.server.rest.security_group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SecurityObjectDto {

    private UUID uuid;
    private String type;
    
}
