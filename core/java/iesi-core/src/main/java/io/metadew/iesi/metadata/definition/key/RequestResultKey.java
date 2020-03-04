package io.metadew.iesi.metadata.definition.key;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestResultKey extends MetadataKey {

    private final String requestId;

}
