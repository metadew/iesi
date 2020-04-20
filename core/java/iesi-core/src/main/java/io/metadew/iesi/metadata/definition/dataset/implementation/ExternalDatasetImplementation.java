package io.metadew.iesi.metadata.definition.dataset.implementation;

import io.metadew.iesi.metadata.definition.dataset.DatasetKey;
import io.metadew.iesi.metadata.definition.dataset.implementation.usage.DatasetUsageStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExternalDatasetImplementation extends DatasetImplementation {

    private final String jdbcConnectionString;
    private final String user;
    private final String password;

    public ExternalDatasetImplementation(DatasetKey metadataKey, DatasetUsageStrategy datasetUsageStrategy, String jdbcConnectionString, String user, String password) {
        super(metadataKey, datasetUsageStrategy);
        this.jdbcConnectionString = jdbcConnectionString;
        this.user = user;
        this.password = password;
    }

}
