package io.metadew.iesi.metadata.definition.component;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComponentVersion extends Metadata<ComponentVersionKey> {

    private String description;
    private List<ComponentBuild> builds;
    @Builder
    public ComponentVersion(ComponentVersionKey componentVersionKey, String description) {
        super(componentVersionKey);
        this.description = description;
        this.builds = new ArrayList<>();
    }

}