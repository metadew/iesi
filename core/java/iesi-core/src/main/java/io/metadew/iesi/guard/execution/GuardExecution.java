package io.metadew.iesi.guard.execution;

public class GuardExecution {

    public GuardExecution() {}

    //Methods
    public void createUser() {
        UserExecution userExecution = new UserExecution();
        userExecution.createUser();
    }

    public void createUser(String userName) throws Exception {

        UserExecution userExecution = new UserExecution();
        userExecution.createUser(userName);

    }

    public void resetPassword(String userName) throws Exception {

        UserExecution userExecution = new UserExecution();
        userExecution.resetPassword(userName);

    }

    public void updateActive(String userName, String status) throws Exception {

        UserExecution userExecution = new UserExecution();
        userExecution.updateActive(userName, status);

    }

    public void updateLocked(String userName, String status) throws Exception {

        UserExecution userExecution = new UserExecution();
        userExecution.updateLocked(userName, status);

    }

    public void resetIndividualLoginFails(String userName) throws Exception {

        UserExecution userExecution = new UserExecution();
        userExecution.resetIndividualLoginFails(userName);

    }

}