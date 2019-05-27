package io.metadew.iesi.metadata.definition;

import java.util.List;

public class UserType {

    private String name;
    private String description;
    private List<UserTypeRole> roles;

    //Constructors
    public UserType() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserTypeRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserTypeRole> roles) {
        this.roles = roles;
    }


}