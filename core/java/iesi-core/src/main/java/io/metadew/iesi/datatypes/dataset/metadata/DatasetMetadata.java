package io.metadew.iesi.datatypes.dataset.metadata;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.framework.FrameworkFolder;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabase;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.File;

@Getter
@EqualsAndHashCode
public class DatasetMetadata {

    private final String datasetName;
    private final Database database;

    public DatasetMetadata(String datasetName) {
        this.datasetName = datasetName;
        this.database = new SqliteDatabase(new SqliteDatabaseConnection(
                FrameworkConfiguration.getInstance().getFrameworkFolder("data")
                        .map(FrameworkFolder::getAbsolutePath)
                        .orElseThrow(() -> new RuntimeException("no definition found for data")) + File.separator + "datasets"
                        + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
    }

    public DatasetMetadata(String datasetName, Database database) {
        this.datasetName = datasetName;
        this.database = database;
    }

}
