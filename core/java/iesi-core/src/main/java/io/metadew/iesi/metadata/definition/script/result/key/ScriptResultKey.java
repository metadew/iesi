package io.metadew.iesi.metadata.definition.script.result.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class ScriptResultKey extends MetadataKey {

    private String runId;
    private Long processId;


}
