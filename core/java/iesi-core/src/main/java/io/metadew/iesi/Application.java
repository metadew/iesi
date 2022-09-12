package io.metadew.iesi;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.launch.AssemblyLauncher;
import io.metadew.iesi.launch.ExecutionLauncher;
import io.metadew.iesi.launch.MetadataLauncher;
import io.metadew.iesi.launch.OpenAPILauncher;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "io.metadew.iesi", "${pluginPackage:#{''}}"})
@Log4j2
public class Application implements ApplicationRunner {

    @Autowired
    private AssemblyLauncher assemblyLauncher;
    @Autowired
    private MetadataLauncher metadataLauncher;
    @Autowired
    private ExecutionLauncher executionLauncher;
    @Autowired
    private OpenAPILauncher openAPILauncher;
    @Autowired
    private FrameworkCrypto frameworkCrypto;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .lazyInitialization(true)
                .run(args);
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
            case "metadata":
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
