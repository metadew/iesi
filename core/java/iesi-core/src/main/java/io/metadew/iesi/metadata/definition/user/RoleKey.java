package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleKey extends MetadataKey {

    private final UUID uuid;

}
