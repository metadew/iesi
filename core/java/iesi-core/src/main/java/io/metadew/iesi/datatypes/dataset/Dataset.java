package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.metadata.definition.SecuredObject;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = DatasetJsonComponent.Deserializer.class)
@JsonSerialize(using = DatasetJsonComponent.Serializer.class)
public class Dataset extends SecuredObject<DatasetKey> {

    private final String name;
    private final Set<DatasetImplementation> datasetImplementations;

    @Builder
    public Dataset(DatasetKey metadataKey, SecurityGroupKey securityGroupKey, String securityGroupName, String name, Set<DatasetImplementation> datasetImplementations) {
        super(metadataKey, securityGroupKey, securityGroupName);
        this.name = name;
        this.datasetImplementations = datasetImplementations;
    }

}
