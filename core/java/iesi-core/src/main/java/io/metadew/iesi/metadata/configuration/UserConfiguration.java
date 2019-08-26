package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserConfiguration {

    private User user;

    // Constructors
    public UserConfiguration() {
    }

    public UserConfiguration(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "select USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL from "
                + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users");
        CachedRowSet crs = MetadataControl.getInstance().getControlMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                users.add(new User(crs.getString("USER_NM"),
                        crs.getString("USER_TYP_NM"),
                        crs.getString("USER_FIRST_NM"),
                        crs.getString("USER_LAST_NM"),
                        crs.getString("USER_ACT_FL"),
                        crs.getString("USER_PWD_HASH"),
                        crs.getString("USER_PWD_EXP_FL"),
                        crs.getLong("LOGIN_FAIL_CUM_NB"),
                        crs.getLong("LOGIN_FAIL_IND_NB"),
                        crs.getString("USER_LOCK_FL")));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("users.error=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("users.stacktrace=" + StackTrace, Level.INFO);
        }
        return users;
    }

    // Delete
    public String getDeleteStatement() {
        return "DELETE FROM " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " WHERE USER_NM = " + SQLTools.GetStringForSQL(this.getUser().getName()) + ";";
    }

    // Delete
    public String getDeleteStatement(User user) {
        return "DELETE FROM " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " WHERE USER_NM = " + SQLTools.GetStringForSQL(user.getName()) + ";";
    }


    public List<String> getInsertStatement(User user) {
        List<String> queries = new ArrayList<>();

        if (this.exists()) {
            queries.add(this.getDeleteStatement(user));
        }

        queries.add("INSERT INTO " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " (USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL) VALUES (" +
                SQLTools.GetStringForSQL(user.getName()) + "," +
                SQLTools.GetStringForSQL(user.getType()) + "," +
                SQLTools.GetStringForSQL(user.getFirstName()) + "," +
                SQLTools.GetStringForSQL(user.getLastName()) + "," +
                SQLTools.GetStringForSQL(user.getActive()) + "," +
                SQLTools.GetStringForSQL(user.getPasswordHash()) + "," +
                SQLTools.GetStringForSQL(user.getExpired()) + "," +
                SQLTools.GetStringForSQL(user.getCumulativeLoginFails()) + "," +
                SQLTools.GetStringForSQL(user.getIndividualLoginFails()) + "," +
                SQLTools.GetStringForSQL(user.getLocked()) + ");");
        return queries;

    }


    // Insert
    public List<String> getInsertStatement() {
        List<String> queries = new ArrayList<>();

        if (this.exists()) {
            queries.add(this.getDeleteStatement());
        }

        queries.add("INSERT INTO " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " (USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL) VALUES (" +
                SQLTools.GetStringForSQL(this.getUser().getName()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getType()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getFirstName()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getLastName()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getActive()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getPasswordHash()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getExpired()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getCumulativeLoginFails()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getIndividualLoginFails()) + "," +
                SQLTools.GetStringForSQL(this.getUser().getLocked()) + ");");
        return queries;
    }

    public String getPasswordStatement() {
        String sql = "";

        sql += "UPDATE " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " SET ";
        sql += "USER_PWD_HASH=";
        sql += SQLTools.GetStringForSQL(this.getUser().getPasswordHash());
        sql += ",";
        sql += "USER_LOCK_FL=";
        sql += SQLTools.GetStringForSQL(this.getUser().getLocked());
        sql += ",";
        sql += "USER_PWD_EXP_FL=";
        sql += SQLTools.GetStringForSQL(this.getUser().getExpired());
        sql += ",";
        sql += "LOGIN_FAIL_IND_NB=";
        sql += SQLTools.GetStringForSQL(this.getUser().getIndividualLoginFails());
        sql += " WHERE ";
        sql += "USER_NM =";
        sql += SQLTools.GetStringForSQL(this.getUser().getName());
        sql += ";";

        return sql;
    }

    public String getActiveUpdateStatement(String userName, String status) {
        return "UPDATE " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " SET USER_ACT_FL=" + SQLTools.GetStringForSQL(status.toUpperCase()) + " WHERE USER_NM = + " +
                SQLTools.GetStringForSQL(userName) + ";";
    }

    public String getBlockedUpdateStatement(String userName, String status) {
        return "UPDATE " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " SET USER_BLOCK_FL=" + SQLTools.GetStringForSQL(status.toUpperCase()) +
                " WHERE USER_NM =" + SQLTools.GetStringForSQL(userName) + ";";
    }

    public String resetIndividualLoginFails(String userName) {
        return "UPDATE " + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users") +
                " SET LOGIN_FAIL_IND_NB=" + SQLTools.GetStringForSQL(0) +
                " WHERE USER_NM =" + SQLTools.GetStringForSQL(userName) + ";";
    }

    // Get User
    public Optional<User> getUser(String userName) {;
        String query = "select USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL from "
                + MetadataControl.getInstance().getControlMetadataRepository().getTableNameByLabel("Users")
                + " where USER_NM = " + SQLTools.GetStringForSQL(userName) + ";";
        CachedRowSet crs = MetadataControl.getInstance().getControlMetadataRepository().executeQuery(query, "reader");
        try {
            if (crs.size() == 0) {
                System.out.println("No Users found " + userName);
                return Optional.empty();
            } else if (crs.size() > 1) {
                // TODO: log
            }
            crs.next();
            User user = new User(userName,
                    crs.getString("USER_TYP_NM"),
                    crs.getString("USER_FIRST_NM"),
                    crs.getString("USER_LAST_NM"),
                    crs.getString("USER_ACT_FL"),
                    crs.getString("USER_PWD_HASH"),
                    crs.getString("USER_PWD_EXP_FL"),
                    crs.getLong("LOGIN_FAIL_CUM_NB"),
                    crs.getLong("LOGIN_FAIL_IND_NB"),
                    crs.getString("USER_LOCK_FL"));
            crs.close();
            return Optional.of(user);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
    }

    // Exists
    public boolean exists() {
        // TODO
        return true;
    }
    // Exists
    public boolean exists(User user) {
        // TODO
        return false;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void insertUser(User user) {
        if (exists(user)) {

        }
        MetadataControl.getInstance().getControlMetadataRepository().executeBatch(getInsertStatement(user));
    }
}