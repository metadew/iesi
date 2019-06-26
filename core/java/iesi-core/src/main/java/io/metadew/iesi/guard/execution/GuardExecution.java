package io.metadew.iesi.guard.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;

public class GuardExecution {

    private FrameworkExecution frameworkExecution;

    public GuardExecution(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    //Methods
    public void createUser() {
        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.createUser();
    }

    public void createUser(String userName) {

        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.createUser(userName);

    }

    public void resetPassword(String userName) {

        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.resetPassword(userName);

    }

    public void updateActive(String userName, String status) {

        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.updateActive(userName, status);

    }

    public void updateLocked(String userName, String status) {

        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.updateLocked(userName, status);

    }

    public void resetIndividualLoginFails(String userName) {

        UserExecution userExecution = new UserExecution(this.getFrameworkExecution());
        userExecution.resetIndividualLoginFails(userName);

    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}