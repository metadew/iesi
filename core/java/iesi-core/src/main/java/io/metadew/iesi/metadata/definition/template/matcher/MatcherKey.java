package io.metadew.iesi.metadata.definition.template.matcher;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class MatcherKey extends MetadataKey {

    private final UUID id;

}