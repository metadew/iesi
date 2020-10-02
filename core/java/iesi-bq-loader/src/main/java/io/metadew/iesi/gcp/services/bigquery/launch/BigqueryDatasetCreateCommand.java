package io.metadew.iesi.gcp.services.bigquery.launch;

import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryConnection;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "create"
)
public class BigqueryDatasetCreateCommand implements Runnable {
    @Option(names = {"-n", "--name"}, required = true, description = "the dataset to create")
    private String datasetName;

    @Override
    public void run() {
        String whichProject = Mount.getInstance().getProjectName("");
        BigqueryConnection.getInstance().createDataset(whichProject, datasetName);
    }
}
