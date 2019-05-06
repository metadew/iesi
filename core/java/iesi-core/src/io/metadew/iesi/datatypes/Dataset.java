package io.metadew.iesi.datatypes;

import io.metadew.iesi.metadata_repository.repository.database.Database;
import io.metadew.iesi.metadata_repository.repository.database.SqliteDatabase;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Dataset extends DataType {

    private final DataType name;
    private final DataType labels;

    private Database dataset;
    private String tableName;
    private Database datasetMetadataConnection;

    public Dataset(DataType name, DataType labels) {
        this.name = name;
        this.labels = labels;
        dataset = getDataset();
    }

    public DataType getDatasetName() {
        return name;
    }

    public DataType getDatasetLabels() {
        return labels;
    }

    public Optional<DataType> getDataItem(String dataItem) {
        CachedRowSet crs;
        String query;
        query = "select value from " +
                tableName +
                " where key = '" + dataItem + "'";

        DataType value = null;
        crs = dataset.executeQuery(query);
        try {
            while (crs.next()) {
                value = DataTypeResolver.resolveToDatatype(crs.getString("VALUE"));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public Map<String, DataType> getDataItems() {
        CachedRowSet crs;
        String query;
        query = "select key, value from " +
                tableName + ";";

        Map<String, DataType> dataItems = new HashMap<>();
        crs = dataset.executeQuery(query);
        try {
            while (crs.next()) {
                dataItems.put(crs.getString("key"), DataTypeResolver.resolveToDatatype(crs.getString("value")));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return dataItems;
        }
        return dataItems;
    }

    public Database getDataset() {
        return getDataset(name, labels);
    }

    private Database getDataset(DataType name, DataType labels) {
        String datasetName = convertDatasetName(name);
        List<String> datasetLabels = convertDatasetLabels(labels);
        return getDataset(datasetName, datasetLabels);
    }

    private List<String> convertDatasetLabels(DataType datasetLabels) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeResolver.resolveToDatatype(datasetLabel.trim()))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            // TODO: log with framework
            System.out.println(MessageFormat.format("Dataset does not accept {0} as type for dataset labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            return datasetName.toString();
        } else {
            // TODO: log
            System.out.println(MessageFormat.format("Dataset does not accept {0} as type for dataset name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            return datasetLabel.toString();
        } else {
            // TODO: log
            System.out.println(MessageFormat.format("Dataset does not accept {0} as type for a dataset label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
    }


    private Database getDatasetMetadata(String datasetName) {
        return new SqliteDatabase(new SqliteDatabaseConnection(datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
    }

    private Database getDataset(String datasetName, List<String> labels) {
        // TODO: should labels be saved in the metadata.db3 as a iesiList or not?
        datasetMetadataConnection = getDatasetMetadata(datasetName);
        String query = "select a.DATASET_INV_ID, a.DATASET_FILE_NM, a.DATASET_TABLE_NM " +
                "from CFG_DATASET_INV a inner join CFG_DATASET_LBL b on a.DATASET_INV_ID = b.DATASET_INV_ID " +
                "where " +
                labels.stream()
                        .map(label -> "b.DATASET_LBL_VAL = '" + label + "'")
                        .collect(Collectors.joining(" and "));
        CachedRowSet cachedRowSet = datasetMetadataConnection.executeQuery(query);

        if (cachedRowSet.size() == 0) {
            tableName = "data";
            return createNewDatasetConnection(datasetName, labels);
        } else if (cachedRowSet.size() == 1) {
            try {
                cachedRowSet.next();
                tableName = cachedRowSet.getString("DATASET_TABLE_NM");
                return new SqliteDatabase(new SqliteDatabaseConnection(datasetName + File.separator + "data" + File.separator + cachedRowSet.getString("DATASET_FILE_NM")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(MessageFormat.format("Found more than one dataset for name '{0}' and labels '{1}'. " +
                            "Returning first occurrence.",
                    name, String.join(", ", labels)));
            try {
                cachedRowSet.next();
                tableName = cachedRowSet.getString("DATASET_TABLE_NM");
                return new SqliteDatabase(new SqliteDatabaseConnection(datasetName + File.separator + "data" + File.separator + cachedRowSet.getString("DATASET_FILE_NM")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // TODO: throw exception and let calling functions (actions) handle this?
        return null;
    }

    private Database createNewDatasetConnection(String datasetName, List<String> labels) {
        String datasetFilename = UUID.randomUUID().toString() + ".db3";
        // register in the metadata
        String sql = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM, DATASET_TABLE_NM) Values (\"" +
                datasetFilename + "\", \"" + String.join(",", labels) + "\", \"data\")";
        datasetMetadataConnection.executeQuery(sql);
        return new SqliteDatabase(new SqliteDatabaseConnection(datasetName + File.separator + "data" + File.separator + datasetFilename));
    }

    @Override
    public String toString() {
        return "{{^dataset(" + name.toString() + ", " + labels.toString() + ")}}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dataset) {
            return name.equals(((Dataset) obj).getDatasetName()) && labels.equals(((Dataset) obj).getDatasetLabels());
        } else {
            return false;
        }
    }
}
