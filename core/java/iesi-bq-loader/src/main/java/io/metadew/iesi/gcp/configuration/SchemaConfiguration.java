package io.metadew.iesi.gcp.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.tools.FileTools;
import io.metadew.iesi.gcp.configuration.cco.rest.MetadataFieldCco;
import io.metadew.iesi.gcp.configuration.cco.rest.MetadataTableCco;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryField;
import io.metadew.iesi.gcp.connection.bigquery.BigquerySchema;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaConfiguration {
    private static final String iesiKeyword = "iesi";
    private static final String metadataKey = "metadata";
    private static final String tableConfigurationKey = "tables";
    private HashMap<String, Object> properties;

    private List<MetadataTableCco> metadataTables;

    public SchemaConfiguration() {
        properties = new HashMap<>();
        this.readSchenaConfiguration();
    }

    public void readSchenaConfiguration() {
        Yaml yaml = new Yaml();
        File file = new File("application-metadata.yml");
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, Object> yamlProperties = yaml.load(inputStream);
        if (yamlProperties.containsKey(iesiKeyword)) {
            update(properties, (Map<String, Object>) yamlProperties.get(iesiKeyword), iesiKeyword);
        } else {

        }

        ObjectMapper objectMapper = new ObjectMapper();
        metadataTables = new ArrayList<>();
        if (!((Map<String, Object>) properties.get(metadataKey)).containsKey(tableConfigurationKey)) {
            System.out.println("no framework setting configuration found on system variable, classpath or filesystem");
        } else {
            List<Object> metadataTableConfigurations =
                    (List<Object>) ((Map<String, Object>) properties
                            .get(metadataKey))
                            .get(tableConfigurationKey);
            for (Object entry : metadataTableConfigurations) {
                MetadataTableCco metadataTable = objectMapper.convertValue(entry, MetadataTableCco.class);
                if (metadataTable.getCategory().equalsIgnoreCase("result")) {
                    metadataTables.add(metadataTable);
                }
            }
        }

        BigquerySchema bigquerySchema = new BigquerySchema();
        for (MetadataTableCco metadataTable : metadataTables) {
            FileTools.delete("bq_" + metadataTable.getName().toLowerCase() + ".json");
            List<BigqueryField> bigqueryFields = new ArrayList<>();
            for (Map.Entry<String, MetadataFieldCco> entry : metadataTable.getFields().entrySet()) {
                MetadataFieldCco metadataField = objectMapper.convertValue(entry.getValue(), MetadataFieldCco.class);
                String _name = entry.getKey();
                String _description = metadataField.getDescription();
                String _type = "";
                String sourceType = metadataField.getType().toLowerCase();
                switch (sourceType) {
                    case "string":
                        _type = "STRING";
                        break;
                    case "number":
                        _type = "INT64";
                        break;
                    case "timestamp":
                        _type = "DATETIME";
                        break;
                    default:
                        _type = "STRING";
                }
                String _mode = "";
                if (metadataField.isNullable()) {
                    _mode = "NULLABLE";
                } else {
                    _mode = "REQUIRED";
                }
                BigqueryField bigqueryField = new BigqueryField(_name,_type,_mode,_description);
                bigqueryFields.add(bigqueryField);
            }
            //write the schema file
            try {
                String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bigqueryFields);
                System.out.println(jsonString);
                FileTools.appendToFile("bq_" + metadataTable.getName().toLowerCase() + ".json","",jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void update(Map<String, Object> original, Map<String, Object> update, String initialKey) {
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            if (original.containsKey(entry.getKey()) && original.get(entry.getKey()) == null) {
                original.put(entry.getKey(), entry.getValue());
            } else if (original.containsKey(entry.getKey())) {
                if (original.get(entry.getKey()).getClass().equals(entry.getValue().getClass())) {
                    if (entry.getValue() instanceof Map) {
                        update((Map<String, Object>) original.get(entry.getKey()), (Map<String, Object>) entry.getValue(), initialKey + "." + entry.getKey());
                    } else {
                        original.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    throw new RuntimeException("original value " + initialKey + original.get(entry.getKey()) + " (" + original.get(entry.getKey()).getClass().getSimpleName() + ")" +
                            " does not match update value " + initialKey + entry.getValue() + " (" + entry.getValue().getClass().getSimpleName() + ")");
                }
            } else {
                original.putAll(update);
            }
        }
    }
}
