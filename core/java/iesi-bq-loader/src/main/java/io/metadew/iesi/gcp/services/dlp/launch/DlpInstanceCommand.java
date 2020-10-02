package io.metadew.iesi.gcp.services.dlp.launch;

import picocli.CommandLine.Command;

@Command(
        name = "instance",
        subcommands = {
                DlpInstanceStartCommand.class
        }
)
public class DlpInstanceCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the dlp instance");

    }
}
