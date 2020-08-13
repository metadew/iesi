package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ScriptKey extends MetadataKey {

    private final String scriptId;
    private final long scriptVersion;

}
