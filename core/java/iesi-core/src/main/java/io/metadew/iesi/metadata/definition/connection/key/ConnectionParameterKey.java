package io.metadew.iesi.metadata.definition.connection.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class ConnectionParameterKey extends MetadataKey {

    private final ConnectionKey connectionKey;
    private final String parameterName;

}
