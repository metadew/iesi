package io.metadew.iesi.datatypes.dataset.metadata;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabase;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@EqualsAndHashCode
public class DatasetMetadata {

    private final String datasetName;
    private final Database database;

    public DatasetMetadata(String datasetName) {
        this.datasetName = datasetName;

        String path1 = (String) Configuration.getInstance()
                .getMandatoryProperty("iesi.home");

        Path path2 = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("data").getAbsolutePath();
        this.database = new SqliteDatabase(new SqliteDatabaseConnection(
                FrameworkConfiguration.getInstance()
                        .getMandatoryFrameworkFolder("data").getAbsolutePath()
                        .resolve("datasets")
                        .resolve(datasetName)
                        .resolve("metadata")
                        .resolve("metadata.db3"),
                null));
    }

    public DatasetMetadata(String datasetName, Database database) {
        this.datasetName = datasetName;
        this.database = database;
    }

}
