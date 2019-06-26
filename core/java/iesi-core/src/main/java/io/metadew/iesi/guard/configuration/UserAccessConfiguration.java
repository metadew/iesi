package io.metadew.iesi.guard.configuration;

import io.metadew.iesi.framework.crypto.Password;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.guard.definition.UserAccess;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.User;

public class UserAccessConfiguration {

    private FrameworkExecution frameworkExecution;

    // Constructors
    public UserAccessConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public UserAccess doUserLogin(String userName, String userPassword) {
        UserAccess userAccess = new UserAccess();
        userAccess.setUserName(userName);

        UserConfiguration userConfiguration = new UserConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        User user = userConfiguration.getUser(userName);

        try {
            if (user == null) {
                userAccess.setLoggedIn(false);
                userAccess.setException(true);
                userAccess.setExceptionMessage("user.unkown");
            } else {
                if (Password.check(userPassword, user.getPasswordHash())) {
                    userAccess.setLoggedIn(true);
                    userAccess.setException(false);
                } else {
                    userAccess.setLoggedIn(false);
                    userAccess.setException(true);
                    userAccess.setExceptionMessage("user.password.incorrect");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userAccess;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    // Getters and Setters

}