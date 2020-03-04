package io.metadew.iesi.datatypes.dataset.metadata;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class DatasetMetadata {

    private final String datasetName;
    private final Database database;

    public DatasetMetadata(String datasetName) {
        this.datasetName = datasetName;
        this.database = new SqliteDatabase(new SqliteDatabaseConnection(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
    }

}
