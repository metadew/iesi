package io.metadew.iesi.gcp.launch;

import io.metadew.iesi.gcp.common.configuration.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

@Command(
        name = "view"
)
public class SpecViewCommand implements Runnable {
    @Parameters
    private List<Path> files;

    @Override
    public void run() {
        if (files != null) {
            files.forEach(path -> Spec.getInstance().readSpec(path));
        }
        System.out.println(Spec.getInstance().toString());
    }
}

