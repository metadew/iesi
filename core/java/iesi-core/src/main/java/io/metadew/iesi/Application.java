package io.metadew.iesi;

import io.metadew.iesi.launch.AssemblyLauncher;
import io.metadew.iesi.launch.ExecutionLauncher;
import io.metadew.iesi.launch.MetadataLauncher;
import io.metadew.iesi.launch.OpenAPILauncher;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

@SpringBootApplication
@Log4j2
public class Application implements ApplicationRunner {

    @Autowired
    @Lazy
    private AssemblyLauncher assemblyLauncher;
    @Autowired
    @Lazy
    private MetadataLauncher metadataLauncher;
    @Autowired
    @Lazy
    private ExecutionLauncher executionLauncher;

    @Autowired
    @Lazy
    private OpenAPILauncher openAPILauncher;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("launcher").hasArg().required().desc("Identify the launcher to execute from on of them: ['assembly', 'metadata', 'execution', 'openapi']").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args.getSourceArgs(), true);

        switch (line.getOptionValue("launcher")) {
            case "assembly":
                assemblyLauncher.execute(line.getArgs());
                break;
            case "metadata" :
                metadataLauncher.execute(line.getArgs());
                break;
            case "execution":
                executionLauncher.execute(line.getArgs());
                break;
            case "openapi":
                openAPILauncher.execute(line.getArgs());
                break;
            default:
                log.info("Not a recognized launcher");
                System.exit(1);
        }
    }
}
