package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptKey extends MetadataKey {

    private final String scriptId;
    private final long scriptVersion;

}
