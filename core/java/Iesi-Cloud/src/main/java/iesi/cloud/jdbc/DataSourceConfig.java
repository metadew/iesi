package iesi.cloud.jdbc;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Bean

	public DataSource createDataSource() {

		JdbcDataSource dataSource = new JdbcDataSource();

		dataSource.setURL("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/database");

		return dataSource;

	}

}