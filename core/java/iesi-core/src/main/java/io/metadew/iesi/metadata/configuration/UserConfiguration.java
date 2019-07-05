package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.User;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserConfiguration {

    private User user;

    private FrameworkInstance frameworkInstance;

    // Constructors
    public UserConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public UserConfiguration(User user, FrameworkInstance frameworkInstance) {
        this.setUser(user);
        this.setFrameworkInstance(frameworkInstance);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "select USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL from "
                + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().executeQuery(query, "reader");
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
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " WHERE USER_NM = " + SQLTools.GetStringForSQL(this.getUser().getName());
        sql += ";";
        sql += "\n";

        return sql;

    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " (USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(this.getUser().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getFirstName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getLastName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getActive());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getPasswordHash());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getExpired());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getCumulativeLoginFails());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getIndividualLoginFails());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getUser().getLocked());
        sql += ")";
        sql += ";";

        return sql;
    }

    public String getPasswordStatement() {
        String sql = "";

        sql += "UPDATE " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
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
        String sql = "";

        sql += "UPDATE " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " SET ";
        sql += "USER_ACT_FL=";
        sql += SQLTools.GetStringForSQL(status.toUpperCase());
        sql += " WHERE ";
        sql += "USER_NM =";
        sql += SQLTools.GetStringForSQL(userName);
        sql += ";";

        return sql;
    }

    public String getBlockedUpdateStatement(String userName, String status) {
        String sql = "";

        sql += "UPDATE " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " SET ";
        sql += "USER_BLOCK_FL=";
        sql += SQLTools.GetStringForSQL(status.toUpperCase());
        sql += " WHERE ";
        sql += "USER_NM =";
        sql += SQLTools.GetStringForSQL(userName);
        sql += ";";

        return sql;
    }

    public String resetIndividualLoginFails(String userName) {
        String sql = "";

        sql += "UPDATE " + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
        sql += " SET ";
        sql += "LOGIN_FAIL_IND_NB=";
        sql += SQLTools.GetStringForSQL(0);
        sql += " WHERE ";
        sql += "USER_NM =";
        sql += SQLTools.GetStringForSQL(userName);
        sql += ";";

        return sql;
    }

    // Get User
    public User getUser(String userName) {
        User user = new User();
        CachedRowSet crs = null;
        String query = "select USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL from "
                + this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users")
                + " where USER_NM = '" + userName + "'";
        crs = this.getFrameworkInstance().getMetadataControl().getControlMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                user.setName(userName);
                user.setType(crs.getString("USER_TYP_NM"));
                user.setFirstName(crs.getString("USER_FIRST_NM"));
                user.setLastName(crs.getString("USER_LAST_NM"));
                user.setActive(crs.getString("USER_ACT_FL"));
                user.setPasswordHash(crs.getString("USER_PWD_HASH"));
                user.setExpired(crs.getString("USER_PWD_EXP_FL"));
                user.setCumulativeLoginFails(crs.getLong("LOGIN_FAIL_CUM_NB"));
                user.setIndividualLoginFails(crs.getLong("LOGIN_FAIL_IND_NB"));
                user.setLocked(crs.getString("USER_LOCK_FL"));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return user;
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}