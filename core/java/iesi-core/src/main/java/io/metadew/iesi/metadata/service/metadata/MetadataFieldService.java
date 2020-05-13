package io.metadew.iesi.metadata.service.metadata;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
@Log4j2
public class MetadataFieldService {

    private static MetadataFieldService INSTANCE;

    public synchronized static MetadataFieldService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataFieldService();
        }
        return INSTANCE;
    }

    private MetadataFieldService() {
    }

    public MetadataField getByName(MetadataTable metadataTable, String name) {
        return metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException("Could not find MetadataTable with name: " + name + " for MetadataTable " + metadataTable));
    }

    public MetadataField getByTableLabelAndName(String label, String name) {
        return getByName(MetadataTableService.getInstance().getByLabel(label), name);
    }

    public String truncateAccordingToConfiguration(String tableLabel, String columnName, String value) {
        MetadataField metadataField = getByTableLabelAndName(tableLabel, columnName);
        if (value.length() > metadataField.getLength()) {
            log.info("truncating value " + value + " to " + metadataField.getLength() + " characters for column " + tableLabel + "/" + columnName);
            return value.substring(0, metadataField.getLength());
        } else {
            return value;
        }
    }

}
