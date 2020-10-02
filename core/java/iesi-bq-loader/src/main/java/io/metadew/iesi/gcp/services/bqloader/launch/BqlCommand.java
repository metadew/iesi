package io.metadew.iesi.gcp.services.bqloader.launch;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
    name = "bql",
    subcommands = {
            BqlInstanceCommand.class,
            BqlPubsubCommand.class,
            BqlBigqueryCommand.class
    }
)
public class BqlCommand implements Runnable {
    public static void main(String[] args) {
        CommandLine.run(new BqlCommand(), args);
    }

    @Override
    public void run() {
        System.out.println("The bql command");
    }
}