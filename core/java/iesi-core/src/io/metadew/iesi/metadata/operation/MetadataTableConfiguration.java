package io.metadew.iesi.metadata.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryCategoryConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.HashMap;
import java.util.Properties;

public class MetadataTableConfiguration {

    private String PRC_RUN_VAR;

    // ExecutionServer
    private String PRC_REQ;

    private String PRC_CTL;

    private String schema;

    private String tableNamePrefix = "";

    private String metadataRepositoryType = "";

    private HashMap<String, String> tableMap;

    // Set metadata repository type
    public MetadataTableConfiguration(FrameworkConfiguration frameworkConfiguration, Properties properties,
                                      FrameworkSettingConfiguration settingsConfig, String metadataRepositoryType,
                                      MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration) {
        this.setMetadataRepositoryType(metadataRepositoryType);
        if (metadataRepositoryCategoryConfiguration != null) {
            this.getTableConfig(frameworkConfiguration, metadataRepositoryCategoryConfiguration, properties, settingsConfig);
        }
    }

    private void getTableConfig(FrameworkConfiguration frameworkConfiguration,
                                MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration, Properties properties,
                                FrameworkSettingConfiguration settingsConfig) {
        // Initialize map
        this.setTableMap(new HashMap<String, String>());

        // Set table name prefix
        String instanceName = (String) properties.get(settingsConfig.getSettingPath("metadata.repository.instance.name"));
        String tempTableNamePrefix = "";
        if (instanceName == null || instanceName.equalsIgnoreCase("")) {
            if (frameworkConfiguration.getFrameworkCode() == null || frameworkConfiguration.getFrameworkCode().equalsIgnoreCase("")) {
                tempTableNamePrefix = "";
            } else {
                tempTableNamePrefix = frameworkConfiguration.getFrameworkCode();
            }
        } else {
            if (frameworkConfiguration.getFrameworkCode() == null || frameworkConfiguration.getFrameworkCode().equalsIgnoreCase("")) {
                tempTableNamePrefix = instanceName;
            } else {
                tempTableNamePrefix = frameworkConfiguration.getFrameworkCode() + "_" + instanceName;
            }
        }
        this.setTableNamePrefix(tempTableNamePrefix);

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(metadataRepositoryCategoryConfiguration.getDefinitionFilePath());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
                MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
                this.getTableMap().put(metadataTable.getLabel(), metadataTable.getName());
            }
        }

        if (this.getMetadataRepositoryType().equalsIgnoreCase("oracle")) {
            this.setSchema(properties.getProperty(settingsConfig.getSettingPath("metadata.repository.oracle.schema").get()));
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("netezza")) {
            this.setSchema(properties.getProperty(settingsConfig.getSettingPath("metadata.repository.netezza.schema").get()));
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("postgresql")) {
            this.setSchema(properties.getProperty(settingsConfig.getSettingPath("metadata.repository.postgresql.schema").get()));
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("sqlite")) {
            this.setSchema("");
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("elasticsearch")) {
            this.setSchema("");
        } else {
            this.setSchema("");
        }

        // Processing Variables
        this.setPRC_RUN_VAR("PRC_RUN_VAR");

        // ExecutionServer
        this.setPRC_REQ("PRC_REQ");
        this.setPRC_CTL("PRC_CTL");
    }

    // Create Getters and Setters

    public String getSchemaPrefix() {
        String tempSchema = "";
        if (this.getMetadataRepositoryType().equalsIgnoreCase("oracle")) {
            tempSchema = this.getSchema();
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("sqlite")) {
            tempSchema = "";
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("elasticsearch")) {
            tempSchema = "";
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("netezza")) {
            tempSchema = this.getSchema();
        } else if (this.getMetadataRepositoryType().equalsIgnoreCase("postgresql")) {
            tempSchema = this.getSchema();
        } else {
            tempSchema = this.getSchema();
            ;
        }

        if (!tempSchema.equalsIgnoreCase("")) {
            return tempSchema += ".";
        } else {
            return tempSchema;
        }

    }

    public String getTableNamePrefix() {
        if (tableNamePrefix.equalsIgnoreCase("")) {
            return "";
        } else {
            return tableNamePrefix.toUpperCase() + "_";
        }
    }

    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    public String getMetadataRepositoryType() {
        return metadataRepositoryType;
    }

    public void setMetadataRepositoryType(String metadataRepositoryType) {
        this.metadataRepositoryType = metadataRepositoryType.toLowerCase();
    }

    public HashMap<String, String> getTableMap() {
        return tableMap;
    }

    private void setTableMap(HashMap<String, String> tableMap) {
        this.tableMap = tableMap;
    }

    public String getTableName(String key) {
        return this.getSchemaPrefix() + this.getTableNamePrefix() + this.getTableMap().get(key);
    }

    public String getPRC_RUN_VAR() {
        return this.getSchemaPrefix() + PRC_RUN_VAR;
    }

    public void setPRC_RUN_VAR(String pRC_RUN_VAR) {
        PRC_RUN_VAR = this.getTableNamePrefix() + pRC_RUN_VAR;
        this.getTableMap().put("PRC_RUN_VAR", PRC_RUN_VAR);
    }

    // Execution Server
    // No schema prefix to return
    // Add type for selecting the schema prefix
    public String getPRC_REQ() {
        return PRC_REQ;
    }

    public void setPRC_REQ(String pRC_REQ) {
        PRC_REQ = pRC_REQ;
        this.getTableMap().put("PRC_REQ", PRC_REQ);
    }

    public String getPRC_CTL() {
        return PRC_CTL;
    }

    public void setPRC_CTL(String pRC_CTL) {
        PRC_CTL = pRC_CTL;
        this.getTableMap().put("PRC_CTL", PRC_CTL);
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}