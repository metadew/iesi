package io.metadew.iesi.gcp.services.dlp.launch;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        name = "dlp",
        subcommands = {
            DlpInstanceCommand.class
        }
)
public class DlpCommand implements Runnable {
    public static void main(String[] args) {
        new CommandLine(new DlpCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("The dlp command");
    }
}