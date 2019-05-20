package io.metadew.iesi.server.rest.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.User;
import io.metadew.iesi.server.rest.controller.FrameworkConnection;

@Component
public class UserSql implements CommandLineRunner {

	private static UserConfiguration userConfiguration = new UserConfiguration(
			FrameworkConnection.getInstance().getFrameworkExecution());

	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/token";
	static final String USER = "sa";
	static final String PASS = "";

	@Override
	public void run(String... args) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			for (User user : userConfiguration.getUsers()) {
				stmt = conn.createStatement();
				String sql = "INSERT INTO users(username,password,enabled) values ('" + user.getName() + "','"
						+ user.getPasswordHash() + "','1' )";
				stmt.executeUpdate(sql);
				String group_id = "1";
				String authority = "AUHTORIZED_ADMIN";
				sql = "INSERT INTO authorities(group_id, authority, username) values ('" + group_id + "','" 
					+ authority + "','" + user.getName() + "')";
				stmt.executeUpdate(sql);
				String enabled = "1";
				sql = "INSERT INTO app_user(username,enabled,password,user_role) values ('" + user.getName() + "', '" + enabled + "','" + user.getPasswordHash() + "', '" + authority + "')";					
				stmt.executeUpdate(sql);
				stmt.close();
			};

			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
}
