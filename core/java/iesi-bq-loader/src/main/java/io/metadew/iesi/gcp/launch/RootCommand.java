package io.metadew.iesi.gcp.launch;

import io.metadew.iesi.gcp.bqloader.launch.BqlCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        subcommands = {
                BqlCommand.class
        }
)
public class RootCommand implements Runnable {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2-gcp.xml");
        CommandLine.run(new RootCommand(), args);
    }

    @Override
    public void run() {
        System.out.println("The iesi.gcp root command");
    }
}