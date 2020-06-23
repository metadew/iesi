package io.metadew.iesi.metadata.definition.template.matcher;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class MatcherKey extends MetadataKey {

    private final String id;

}