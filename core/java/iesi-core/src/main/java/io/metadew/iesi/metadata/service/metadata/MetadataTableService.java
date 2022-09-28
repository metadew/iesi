package io.metadew.iesi.metadata.service.metadata;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;
import org.springframework.stereotype.Service;

@Service
public class MetadataTableService {

    private final MetadataTablesConfiguration metadataTablesConfiguration;

    public MetadataTableService(MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }

    public MetadataTable getByLabel(String label) {
        return metadataTablesConfiguration.getMetadataTables().stream()
                .filter(entry -> entry.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find MetadataTable with label: " + label));
    }

}
