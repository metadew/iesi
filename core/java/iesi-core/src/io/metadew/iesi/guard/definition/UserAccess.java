package io.metadew.iesi.guard.definition;

public class UserAccess {

    private String userName;
    private String spaceName;
    private boolean loggedIn;
    private boolean exception = false;
    private String exceptionMessage;

    // Constructors
    public UserAccess() {

    }

    // Getters and Setters
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exception = true;
        this.exceptionMessage = exceptionMessage;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }


}