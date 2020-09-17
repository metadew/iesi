package io.metadew.iesi.gcp.bqloader.launch;

import picocli.CommandLine.Command;

@Command(
        name = "instance",
        subcommands = {
                BqlInstanceCreateCommand.class
        }
)

public class BqlInstanceCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the instance setup");
    }
}