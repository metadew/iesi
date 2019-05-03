package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.User;

public class UserConfiguration
{

	private User user;

	private FrameworkExecution frameworkExecution;

	// Constructors
	public UserConfiguration(FrameworkExecution frameworkExecution)
	{
		this.setFrameworkExecution(frameworkExecution);
	}

	public UserConfiguration(User user, FrameworkExecution frameworkExecution)
	{
		this.setUser(user);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Delete
	public String getDeleteStatement()
	{
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
		sql += " WHERE USER_NM = " + SQLTools.GetStringForSQL(this.getUser().getName());
		sql += ";";
		sql += "\n";

		return sql;

	}

	// Insert
	public String getInsertStatement()
	{
		String sql = "";

		if (this.exists())
		{
			sql += this.getDeleteStatement();
		}

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
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

	public String getPasswordStatement()
	{
		String sql = "";

		sql += "UPDATE " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
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

	public String getActiveUpdateStatement(String userName, String status)
	{
		String sql = "";

		sql += "UPDATE " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
		sql += " SET ";
		sql += "USER_ACT_FL=";
		sql += SQLTools.GetStringForSQL(status.toUpperCase());
		sql += " WHERE ";
		sql += "USER_NM =";
		sql += SQLTools.GetStringForSQL(userName);
		sql += ";";

		return sql;
	}

	public String getBlockedUpdateStatement(String userName, String status)
	{
		String sql = "";

		sql += "UPDATE " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
		sql += " SET ";
		sql += "USER_BLOCK_FL=";
		sql += SQLTools.GetStringForSQL(status.toUpperCase());
		sql += " WHERE ";
		sql += "USER_NM =";
		sql += SQLTools.GetStringForSQL(userName);
		sql += ";";

		return sql;
	}

	public String resetIndividualLoginFails(String userName)
	{
		String sql = "";

		sql += "UPDATE " + this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users");
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
	public User getUser(String userName)
	{
		User user = new User();
		CachedRowSet crs = null;
		String query = "select USER_NM, USER_TYP_NM, USER_FIRST_NM, USER_LAST_NM, USER_ACT_FL, USER_PWD_HASH, USER_PWD_EXP_FL, LOGIN_FAIL_CUM_NB, LOGIN_FAIL_IND_NB, USER_LOCK_FL from "
					+ this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().getTableNameByLabel("Users")
					+ " where USER_NM = '" + userName + "'";
		crs = this.getFrameworkExecution().getMetadataControl().getControlMetadataRepository().executeQuery(query, "reader");
		try
		{
			while (crs.next())
			{
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
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		
		if (user.getName() == null) {
			return null;
		} else {
			return user;	
		}
	}

	// Exists
	public boolean exists()
	{
		return true;
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

}