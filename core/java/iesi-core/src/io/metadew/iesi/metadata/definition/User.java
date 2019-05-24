package io.metadew.iesi.metadata.definition;

public class User {

    private String name;
    private String type;
    private String firstName;
    private String lastName;
    private String active;
    private String passwordHash;
    private String expired;
    private Long cumulativeLoginFails;
    private Long individualLoginFails;
    private String locked;

    //Constructors
    public User(String name, String type, String firstName, String lastName, String active, String passwordHash,
                String expired, Long cumulativeLoginFails, Long individualLoginFails, String locked) {
        this.name = name;
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.passwordHash = passwordHash;
        this.expired = expired;
        this.cumulativeLoginFails = cumulativeLoginFails;
        this.individualLoginFails = individualLoginFails;
        this.locked = locked;
    }

    public User() {

    }

    //Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public Long getCumulativeLoginFails() {
        return cumulativeLoginFails;
    }

    public void setCumulativeLoginFails(Long cumulativeLoginFails) {
        this.cumulativeLoginFails = cumulativeLoginFails;
    }

    public Long getIndividualLoginFails() {
        return individualLoginFails;
    }

    public void setIndividualLoginFails(Long individualLoginFails) {
        this.individualLoginFails = individualLoginFails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

}