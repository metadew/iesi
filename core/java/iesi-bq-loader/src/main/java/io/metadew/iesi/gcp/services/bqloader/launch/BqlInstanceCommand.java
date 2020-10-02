package io.metadew.iesi.gcp.services.bqloader.launch;

import picocli.CommandLine.Command;

@Command(
        name = "instance",
        subcommands = {
                BqlInstanceCreateCommand.class,
                BqlInstanceStartCommand.class
        }
)

public class BqlInstanceCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the instance setup");
    }
}