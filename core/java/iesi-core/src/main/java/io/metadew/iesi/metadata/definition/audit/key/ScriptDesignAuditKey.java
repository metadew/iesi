package io.metadew.iesi.metadata.definition.audit.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ScriptDesignAuditKey extends MetadataKey {

    private final UUID ID;

}
