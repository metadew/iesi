package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityKey extends MetadataKey {

    private final UUID uuid;

}
