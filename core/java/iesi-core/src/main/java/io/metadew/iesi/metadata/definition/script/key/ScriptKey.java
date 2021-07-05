package io.metadew.iesi.metadata.definition.script.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ScriptKey extends MetadataKey {

    private final String scriptId;
    // private String deletedAt;

    public ScriptKey(String scriptId) {
        this.scriptId = scriptId;
        // this.deletedAt = "NA";
    }

//    public ScriptKey(String scriptId, String deletedAt) {
//        this.scriptId = scriptId;
//        // this.deletedAt = deletedAt;
//    }

}
