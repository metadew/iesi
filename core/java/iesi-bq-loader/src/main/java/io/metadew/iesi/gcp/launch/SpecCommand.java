package io.metadew.iesi.gcp.launch;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        name = "spec",
        subcommands = {
                SpecCreateCommand.class,
                SpecDeleteCommand.class,
                SpecViewCommand.class
        }
)
public class SpecCommand implements Runnable {
    public static void main(String[] args) {
        new CommandLine(new SpecCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("The spec command");
    }
}