package io.metadew.iesi.datatypes.dataset.keyvalue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabase;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.DatasetService;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.metadata.DatasetMetadata;
import io.metadew.iesi.datatypes.dataset.metadata.DatasetMetadataService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class KeyValueDatasetService extends DatasetService<KeyValueDataset> implements IDatasetService<KeyValueDataset> {

    private static KeyValueDatasetService INSTANCE;

    private MetadataTable metadataTable = new MetadataTable("data", "dataset", "dataset", "dataset",
            Stream.of(
                    new AbstractMap.SimpleEntry<>("key", new MetadataField("key", 1, "string", 2000, false, false, true)),
                    new AbstractMap.SimpleEntry<>("value", new MetadataField("key", 1, "string", 2000, false, false, true))
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    public synchronized static KeyValueDatasetService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KeyValueDatasetService();
        }
        return INSTANCE;
    }

    private KeyValueDatasetService() {
    }

    public KeyValueDataset getByNameAndLabels(String name, List<String> labels, ExecutionRuntime executionRuntime) {
        name = executionRuntime.resolveVariables(name);
        labels = labels.stream()
                .map(executionRuntime::resolveVariables)
                .collect(Collectors.toList());
        DatasetMetadata datasetMetadata = new DatasetMetadata(name);
        if (labels.isEmpty()) {
            throw new RuntimeException("Dataset " + name + "should at least contain 1 label");
        }
        Optional<Long> id = DatasetMetadataService.getInstance().getId(datasetMetadata, labels);
        if (!id.isPresent()) {
            return createNewDataset(name, labels);
        } else {
            long datasetInventoryId = id.get();
            String tableName = DatasetMetadataService.getInstance().getTableName(datasetMetadata, datasetInventoryId);
            Database datasetDatabase = DatasetMetadataService.getInstance().getDatasetDatabase(datasetMetadata, datasetInventoryId);
            return new KeyValueDataset(name, labels, datasetMetadata, datasetDatabase, tableName);
        }
    }

    @Override
    public void shutdown(KeyValueDataset dataset) {
        DatabaseHandler.getInstance().shutdown(dataset.getDatasetDatabase());
        DatasetMetadataService.getInstance().shutdown(dataset.getDatasetMetadata());
    }

    @Synchronized
    public KeyValueDataset createNewDataset(String name, List<String> labels) {
        DatasetMetadata datasetMetadata = DatasetMetadataService.getInstance().getByName(name);
        log.trace(MessageFormat.format("datatype.dataset=initializing new dataset database for ''{0}'' with labels {1}", name, labels.toString()));
        int nextInventoryId = DatasetMetadataService.getInstance().getLatestInventoryId(datasetMetadata) + 1;
        String datasetFilename = UUID.randomUUID().toString() + ".db3";
        String tableName = "data";
        DatasetMetadataService.getInstance().insertDatasetLabelInformation(datasetMetadata, nextInventoryId, labels);

        log.debug(MessageFormat.format("creating dataset {0} for {1} at {2} table {3}", nextInventoryId, name, datasetFilename, tableName));
        Path filepath = FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder(tableName).getAbsolutePath()
                .resolve("datasets")
                .resolve(name)
                .resolve(tableName)
                .resolve(datasetFilename);
        File file = filepath.toFile();
        file.setWritable(true, true);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatasetMetadataService.getInstance().insertDatasetDatabaseInformation(datasetMetadata, nextInventoryId, datasetFilename, tableName);
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(filepath.toString()));
        DatabaseHandler.getInstance().createTable(database, metadataTable);
        // String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(tableName) + " (key TEXT, value TEXT)";
        // DatabaseHandlerImpl.getInstance().executeUpdate(database, create);
        return new KeyValueDataset(name, labels, datasetMetadata, database, tableName);
    }

    private KeyValueDataset getObjectDataset(KeyValueDataset dataset, String keyPrefix) throws IOException {
        if (keyPrefix != null) {
            List<String> labels = new ArrayList<>(dataset.getLabels());
            labels.add(keyPrefix);
            return createNewDataset(dataset.getName(), labels);
        } else {
            return dataset;
        }
    }

    public void writeRawJSON(KeyValueDataset dataset, String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(json);
            writeRawJSON(dataset, jsonNode);
        } catch (IOException e) {
            // log.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", field.getValue().getNodeType().toString()));
        }
    }

    public void writeRawJSON(KeyValueDataset dataset, JsonNode jsonNode) {
        log.debug(MessageFormat.format("writing raw json ''{0}'' to dataset {1}", jsonNode.toString(), dataset.toString()));
        writeRawJSON(dataset, jsonNode, "");
    }

    private void writeRawJSON(KeyValueDataset dataset, JsonNode jsonNode, String keyPrefix) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getValue().getNodeType().equals(JsonNodeType.OBJECT)) {
                writeRawJSON(dataset, field.getValue(), keyPrefix + field.getKey() + ".");
            } else if (field.getValue().getNodeType().equals(JsonNodeType.ARRAY)) {
                int arrayCounter = 1;
                for (JsonNode element : field.getValue()) {
                    writeRawJSON(dataset, element, keyPrefix + field.getKey() + "." + arrayCounter + ".");
                    arrayCounter++;
                }
            } else if (field.getValue().getNodeType().equals(JsonNodeType.NULL)) {
                DatasetHandler.getInstance().setDataItem(dataset, keyPrefix + field.getKey(), new Text(""));
            } else if (field.getValue().isValueNode()) {
                DatasetHandler.getInstance().setDataItem(dataset, keyPrefix + field.getKey(), new Text(field.getValue().asText()));
            } else {
                log.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", field.getValue().getNodeType().toString()));
            }
        }
    }

    public KeyValueDataset resolve(String arguments, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Dataset", arguments));
        arguments = executionRuntime.resolveVariables(arguments);
        List<String> splittedArguments = DataTypeHandler.getInstance().splitInstructionArguments(arguments);
        if (splittedArguments.size() == 2) {
            List<DataType> resolvedArguments = splittedArguments.stream()
                    .map(argument -> DataTypeHandler.getInstance().resolve(argument, executionRuntime))
                    .collect(Collectors.toList());
            return getByNameAndLabels(
                    DatasetHandler.getInstance().convertDatasetName(resolvedArguments.get(0)),
                    DatasetHandler.getInstance().convertDatasetLabels(resolvedArguments.get(1), executionRuntime),
                    executionRuntime);
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
        }
    }

    public void write(KeyValueDataset dataset, JsonNode jsonNode, ExecutionRuntime executionRuntime) throws IOException {
        DataTypeHandler.getInstance().resolve(dataset, null, jsonNode, executionRuntime);
    }

    public DataType resolve(KeyValueDataset dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime) throws IOException {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        KeyValueDataset objectDataset = getObjectDataset(dataset, key);
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            DataType object = DataTypeHandler.getInstance().resolve(objectDataset, field.getKey(), field.getValue(), executionRuntime);
            DatasetHandler.getInstance().setDataItem(objectDataset, field.getKey(), object);
        }
        return objectDataset;
    }

    @Override
    public void clean(KeyValueDataset keyValueDataset, ExecutionRuntime executionRuntime) {
        log.debug(MessageFormat.format("cleaning keyValueDataset {0}:{1}", keyValueDataset.getName(), String.join("-", keyValueDataset.getLabels())));
        for (DataType dataType : getDataItems(keyValueDataset, executionRuntime).values()) {
            clean(dataType, executionRuntime);
        }
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = " + SQLTools.GetStringForSQLTable(keyValueDataset.getTableName()) + ";";
        try {
            CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(keyValueDataset.getDatasetDatabase(), queryTableExists);
            if (crs.size() >= 1) {
                crs.next();
                String clean = "delete from " + SQLTools.GetStringForSQLTable(keyValueDataset.getTableName()) + ";";
                DatabaseHandler.getInstance().executeUpdate(keyValueDataset.getDatasetDatabase(), clean);
            } else {
                String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(keyValueDataset.getTableName()) + " (key TEXT, value TEXT);";
                DatabaseHandler.getInstance().executeUpdate(keyValueDataset.getDatasetDatabase(), create);
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace);
        }
    }

    public void clean(DataType dataType, ExecutionRuntime executionRuntime) {
        if (dataType instanceof Array) {
            for (DataType element : ((Array) dataType).getList()) {
                clean(element, executionRuntime);
            }
        } else if (dataType instanceof Dataset) {
            DatasetHandler.getInstance().clean((Dataset) dataType, executionRuntime);
        }
    }

    @Override
    public Optional<DataType> getDataItem(KeyValueDataset dataset, String dataItem, ExecutionRuntime executionRuntime) {
        String query = "select value from " + SQLTools.GetStringForSQLTable(dataset.getTableName()) + " where key = " + SQLTools.GetStringForSQL(dataItem) + ";";
        try {
            CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(dataset.getDatasetDatabase(), query);
            if (crs.size() == 0) {
                return Optional.empty();
            } else if (crs.size() > 1) {
                log.warn(MessageFormat.format("Dataset contains multiple items with key ''{0}''. Returning first value", dataItem));
            }
            crs.next();
            DataType value = DataTypeHandler.getInstance().resolve(crs.getString("VALUE"), executionRuntime);
            crs.close();
            return Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, DataType> getDataItems(KeyValueDataset dataset, ExecutionRuntime executionRuntime) {
        Map<String, DataType> dataItems = new HashMap<>();
        try {
            String query = "select key, value from " + SQLTools.GetStringForSQLTable(dataset.getTableName()) + ";";
            CachedRowSet crs = DatabaseHandler.getInstance().executeQuery(dataset.getDatasetDatabase(), query);
            while (crs.next()) {
                dataItems.put(crs.getString("key"), DataTypeHandler.getInstance().resolve(crs.getString("value"), executionRuntime));
            }
            crs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataItems;
    }

    @Override
    public void setDataItem(KeyValueDataset dataset, String key, DataType value) {
        String query = "insert into " + SQLTools.GetStringForSQLTable(dataset.getTableName()) + " (key, value) values ("
                + SQLTools.GetStringForSQL(key) + ", " + SQLTools.GetStringForSQL(value.toString()) + ");";
        DatabaseHandler.getInstance().executeUpdate(dataset.getDatasetDatabase(), query);
    }

    @Override
    public Class<KeyValueDataset> appliesTo() {
        return KeyValueDataset.class;
    }

    @Override
    public String keyword() {
        return "dataset";
    }
}
