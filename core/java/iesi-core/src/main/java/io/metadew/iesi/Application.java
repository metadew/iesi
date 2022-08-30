package io.metadew.iesi;

import io.metadew.iesi.launch.AssemblyLauncher;
import io.metadew.iesi.launch.ExecutionLauncher;
import io.metadew.iesi.launch.MetadataLauncher;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Arrays;

@SpringBootApplication
@Log4j2
public class Application implements ApplicationRunner {

    private final AssemblyLauncher assemblyLauncher;
    private final MetadataLauncher metadataLauncher;
    private final ExecutionLauncher executionLauncher;

    public Application(AssemblyLauncher assemblyLauncher,
                       MetadataLauncher metadataLauncher,
                       ExecutionLauncher executionLauncher) {
        this.assemblyLauncher = assemblyLauncher;
        this.metadataLauncher = metadataLauncher;
        this.executionLauncher = executionLauncher;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String launcherSelected = args.getSourceArgs()[1];
        String[] launcherOptions = Arrays.copyOfRange(args.getSourceArgs(), 2, args.getSourceArgs().length);

        switch (launcherSelected) {
            case "assembly":
                assemblyLauncher.execute(launcherOptions);
                break;
            case "metadata" :
                metadataLauncher.execute(launcherOptions);
                break;
            case "execution":
                executionLauncher.execute(launcherOptions);
                break;
            default:
                log.info("Not a recognized launcher");
                System.exit(1);
        }

        System.exit(0);
    }
}
