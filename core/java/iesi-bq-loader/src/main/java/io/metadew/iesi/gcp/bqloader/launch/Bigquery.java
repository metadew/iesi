package io.metadew.iesi.gcp.bqloader.launch;

import io.metadew.iesi.gcp.bqloader.bigquery.Dataset;
import io.metadew.iesi.gcp.bqloader.bigquery.Table;
import org.apache.commons.cli.*;

public class Bigquery {

    public static void main( String[] args ) {

        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("create").desc("create the bigquery dataset and table").build())
                .addOption(Option.builder("delete").desc("delete the bigquery dataset and table").build());

        // create the parser
        CommandLineParser parser = new DefaultParser();

        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("Incorrect format provided, see --help for more info");
        }

        boolean create = false;
        boolean delete = false;

        if (line != null) {
            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            if (line.hasOption("create")) create = true;
            if (line.hasOption("delete")) delete = true;
        }

        Dataset dataset = new Dataset("iesi-01","iesi_results");
        Table table = new Table(dataset, "foo");
        if (create) {
            dataset.create();
            table.create();
        }

        if (delete) {
            table.delete();
            dataset.delete();
        }

    }


}
