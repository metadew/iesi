package io.metadew.iesi.guard.configuration;

import io.metadew.iesi.framework.crypto.Password;
import io.metadew.iesi.guard.definition.UserAccess;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public class UserAccessConfiguration {


    // Constructors
    public UserAccessConfiguration() {
    }

    public UserAccess doUserLogin(String userName, String userPassword) {
        UserAccess userAccess = new UserAccess();
        userAccess.setUserName(userName);

        UserConfiguration userConfiguration = new UserConfiguration();
        Optional<User> user = userConfiguration.getUser(userName);

        try {
            if (!user.isPresent()) {
                userAccess.setLoggedIn(false);
                userAccess.setException(true);
                userAccess.setExceptionMessage("user.unknown");
            } else {
                if (Password.check(userPassword, user.get().getPasswordHash())) {
                    userAccess.setLoggedIn(true);
                    userAccess.setException(false);
                } else {
                    userAccess.setLoggedIn(false);
                    userAccess.setException(true);
                    userAccess.setExceptionMessage("user.password.incorrect");
                }
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return userAccess;
    }

}