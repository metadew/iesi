package io.metadew.iesi.guard.execution;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.crypto.Password;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.User;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class UserExecution {

    public UserExecution() {}

    // Methods
    public void createUser() {
        User user = new User();
        user.setName(this.getInput("Enter username (name@domain.ext)"));
        user.setFirstName(this.getInput("Enter first name"));
        user.setLastName(this.getInput("Enter last name"));
        System.out.println("user: " + this.getInput("dfdf"));
    }

    public User createUser(String userName) {
            User user = new User();
            user.setName(userName);
            user.setFirstName(this.getInput("Enter first name"));
            user.setLastName(this.getInput("Enter last name"));
            user.setActive("Y");
            user.setExpired("Y");
            user.setCumulativeLoginFails(0L);
            user.setIndividualLoginFails(0L);
            user.setLocked("N");
            user.setType("user");
        try {
            user.setPasswordHash(Password.getSaltedHash("ok"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        // user.setPasswordHash(Password.getSaltedHash(this.getPassword()));

            UserConfiguration userConfiguration = new UserConfiguration();
            MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository()
                    .executeBatch(userConfiguration.getInsertStatement(user));

            return user;
    }

    public void resetPassword(String userName) {
        try {
            User user = new User();
            user.setName(userName);
            user.setExpired("Y");
            user.setLocked("N");
            user.setIndividualLoginFails(0L);
            user.setPasswordHash(Password.getSaltedHash("ok"));
            // user.setPasswordHash(Password.getSaltedHash(this.getPassword()));

            UserConfiguration userConfiguration = new UserConfiguration(user);
            MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository()
                    .executeUpdate(userConfiguration.getPasswordStatement());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void updateActive(String userName, String status) {
        UserConfiguration userConfiguration = new UserConfiguration();
        MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository()
                .executeUpdate(userConfiguration.getActiveUpdateStatement(userName, status));
    }

    public void updateLocked(String userName, String status) {
        UserConfiguration userConfiguration = new UserConfiguration();
        MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository()
                .executeUpdate(userConfiguration.getBlockedUpdateStatement(userName, status));
    }

    public void resetIndividualLoginFails(String userName) {
        UserConfiguration userConfiguration = new UserConfiguration();
        MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository()
                .executeUpdate(userConfiguration.resetIndividualLoginFails(userName));
    }

    @SuppressWarnings("unused")
    private String getPassword() {
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(1);
        }
        char passwordArray[] = console.readPassword("Enter password: ");

        return new String(passwordArray);
    }

    @SuppressWarnings("resource")
    private String getInput(String question) {
        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        // prompt
        String prompt = null;
        if (question != null && !question.isEmpty()) {
            prompt = question + ": ";
        } else {
            prompt = "Do you confirm to proceed? [Y]/STOP ";
        }
        System.out.print(prompt);

        // Get Input
        boolean getInput = false;
        String readInput = null;
        while (!getInput) {
            readInput = null;
            if ((readInput = scanner.nextLine()).isEmpty()) {
                getInput = false;
            }

            if (!getInput) {
                if (!readInput.equalsIgnoreCase("") || readInput.equalsIgnoreCase("stop")) {
                    getInput = true;
                } else {
                    System.out.print(prompt);
                }
            }
        }

        // Log result
        // this.getActionExecution().getActionControl().logOutput("confirmation",
        // readInput);

        // Stopping process on user request
        if (readInput.equalsIgnoreCase("STOP")) {
            return "";
        } else {
            return readInput;
        }

    }

}