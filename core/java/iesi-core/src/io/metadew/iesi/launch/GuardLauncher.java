package io.metadew.iesi.launch;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkExecutionSettings;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.guard.execution.GuardExecution;
import io.metadew.iesi.metadata.definition.Context;
import org.apache.commons.cli.*;

/**
 * The guard launcher is entry point to launch all access control actions.
 *
 * @author peter.billen
 */
public class GuardLauncher {

    public static void main(String[] args) {

        Option oHelp = new Option("help", "print this message");
        Option oUser = new Option("user", true, "define the user name");
        Option oCreate = new Option("create", "create a new user");
        Option oPassword = new Option("password", "reset a user password");
        Option oActive = new Option("active", true, "switch a user to (in)active");
        Option oLocked = new Option("locked", true, "(un)block a user");
        Option oReset = new Option("reset", "resets the individual login fail counter");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oUser);
        options.addOption(oCreate);
        options.addOption(oPassword);
        options.addOption(oActive);
        options.addOption(oLocked);
        options.addOption(oReset);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        String settings = "";
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            // Calling the launch controller
            System.out.println();
            System.out.println("guard.launcher.start");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            // Create the framework instance
            FrameworkInstance frameworkInstance = new FrameworkInstance();

            // Create the framework execution
            FrameworkExecutionSettings frameworkExecutionSettings = new FrameworkExecutionSettings(settings);
            Context context = new Context();
            context.setName("guard");
            context.setScope("user");
            FrameworkExecution frameworkExecution = new FrameworkExecution(frameworkInstance,
                    new FrameworkExecutionContext(context), frameworkExecutionSettings, null, null);

            String userName = "";
            String active = "";
            String locked = "";
            if (line.hasOption("user")) {
                userName = line.getOptionValue("user");
                System.out.println("Option -user (user) value = " + userName);

                if (line.hasOption("create")) {
                    GuardExecution guardExecution = new GuardExecution(frameworkExecution);
                    guardExecution.createUser(userName);
                }

                if (line.hasOption("password")) {
                    GuardExecution guardExecution = new GuardExecution(frameworkExecution);
                    guardExecution.resetPassword(userName);
                }

                if (line.hasOption("active")) {
                    active = line.getOptionValue("active");
                    GuardExecution guardExecution = new GuardExecution(frameworkExecution);
                    guardExecution.updateActive(userName, active);
                }

                if (line.hasOption("locked")) {
                    locked = line.getOptionValue("locked");
                    GuardExecution guardExecution = new GuardExecution(frameworkExecution);
                    guardExecution.updateLocked(userName, locked);
                }

                if (line.hasOption("reset")) {
                    GuardExecution guardExecution = new GuardExecution(frameworkExecution);
                    guardExecution.resetIndividualLoginFails(userName);
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}