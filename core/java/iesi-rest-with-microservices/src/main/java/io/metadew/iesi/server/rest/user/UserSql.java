package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class UserSql implements CommandLineRunner {

	@Autowired
	private UserConfiguration userConfiguration;

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
				String enabled = "1";
				String sql = "INSERT INTO users(username,password,enabled) values ('" + user.getName() + "','"
						+ user.getPasswordHash() + "','" + enabled + "')";
				stmt.executeUpdate(sql);
				String group_id = "1";
				String authority = "AUTHORIZED_USER";
				sql = "INSERT INTO authorities(group_id, authority, username) values ('" + group_id + "','" 
					+ authority + "','" + user.getName() + "')";
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
