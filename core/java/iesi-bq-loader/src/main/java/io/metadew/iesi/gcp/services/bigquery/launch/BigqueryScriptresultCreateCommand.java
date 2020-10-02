package io.metadew.iesi.gcp.services.bigquery.launch;

import io.metadew.iesi.gcp.configuration.bigquery.ScriptResultConfiguration;
import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryConnection;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "create"
)
public class BigqueryScriptresultCreateCommand implements Runnable {
    @Option(names = {"-d", "--dataset"}, required = true, description = "the dataset where to create the table")
    private String datasetName;

    @Override
    public void run() {
        String whichProject = Mount.getInstance().getProjectName("");
        BigqueryConnection.getInstance().createTable(whichProject, datasetName, new ScriptResultConfiguration());
    }
}
