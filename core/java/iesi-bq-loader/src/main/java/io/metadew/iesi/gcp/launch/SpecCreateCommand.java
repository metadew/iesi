package io.metadew.iesi.gcp.launch;

import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.configuration.bigquery.ScriptExecutionConfiguration;
import io.metadew.iesi.gcp.configuration.bigquery.ScriptResultConfiguration;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryConnection;
import io.metadew.iesi.gcp.services.pubsub.common.PubsubService;
import io.metadew.iesi.gcp.spec.bigquery.BigqueryDatasetSpec;
import io.metadew.iesi.gcp.spec.bigquery.BigquerySpec;
import io.metadew.iesi.gcp.spec.bigquery.BigqueryTableSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

@Command(
        name = "create"
)
public class SpecCreateCommand implements Runnable {
    @Parameters
    private List<Path> files;

    @Override
    public void run() {
        String whichProject = Mount.getInstance().getProjectName("");

        if (files != null) {
            files.forEach(path -> Spec.getInstance().readSpec(path));
        }

        //Create pubsub
        for (PubsubSpec pubsubSpec : Spec.getInstance().getGcpSpec().getPubsub()) {
            PubsubService.getInstance().init(whichProject,pubsubSpec.getName());
            PubsubService.getInstance().create();
        }

        //Create Bigquery
        for (BigquerySpec bigquerySpec : Spec.getInstance().getGcpSpec().getBigquery()) {
            for (BigqueryDatasetSpec bigqueryDatasetSpec : bigquerySpec.getDatasets()) {
                BigqueryConnection.getInstance().createDataset(whichProject, bigqueryDatasetSpec.getName());

                for (BigqueryTableSpec bigqueryTableSpec : bigqueryDatasetSpec.getTables()) {
                    if (bigqueryTableSpec.getName().equalsIgnoreCase("scriptresult")) {
                        BigqueryConnection.getInstance().createTable(whichProject, bigqueryDatasetSpec.getName(), new ScriptResultConfiguration());
                    }

                    if (bigqueryTableSpec.getName().equalsIgnoreCase("scriptexecution")) {
                        BigqueryConnection.getInstance().createTable(whichProject, bigqueryDatasetSpec.getName(), new ScriptExecutionConfiguration());
                    }
                }
            }

        }

    }
}

