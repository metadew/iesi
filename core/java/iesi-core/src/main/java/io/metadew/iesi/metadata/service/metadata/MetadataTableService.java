package io.metadew.iesi.metadata.service.metadata;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;

public class MetadataTableService {

    private static MetadataTableService INSTANCE;

    public synchronized static MetadataTableService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataTableService();
        }
        return INSTANCE;
    }

    private MetadataTableService() {
    }

    public MetadataTable getByLabel(String label) {
        return MetadataTablesConfiguration.getInstance().getMetadataTables().stream()
                .filter(entry -> entry.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find MetadataTable with label: " + label));
    }

}
