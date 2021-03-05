package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class ImpersonationKey extends MetadataKey {

    private final String name;

}
