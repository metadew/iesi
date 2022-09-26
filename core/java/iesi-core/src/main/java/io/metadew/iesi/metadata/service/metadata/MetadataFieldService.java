package io.metadew.iesi.metadata.service.metadata;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@Log4j2
public class MetadataFieldService {

    private final MetadataTableService metadataTableService;

    public MetadataFieldService(MetadataTableService metadataTableService) {
        this.metadataTableService = metadataTableService;
    }

    public MetadataField getByName(MetadataTable metadataTable, String name) {
        return metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException("Could not find MetadataTable with name: " + name + " for MetadataTable " + metadataTable));
    }

    public MetadataField getByTableLabelAndName(String label, String name) {
        return getByName(metadataTableService.getByLabel(label), name);
    }

    public String truncateAccordingToConfiguration(String tableLabel, String columnName, String value) {
        if (value == null) return null;
        MetadataField metadataField = getByTableLabelAndName(tableLabel, columnName);
        if (value.length() > metadataField.getLength()) {
            log.info("truncating value " + value + " to " + metadataField.getLength() + " characters for column " + tableLabel + "/" + columnName);
            return value.substring(0, metadataField.getLength());
        } else {
            return value;
        }
    }

}
