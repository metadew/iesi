//package io.metadew.iesi.server.rest;
//
//import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
//import io.metadew.iesi.metadata.definition.Connection;
//import io.metadew.iesi.metadata.definition.ConnectionParameter;
//import io.metadew.iesi.server.rest.controller.ConnectionsController;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(ConnectionsController.class)
//public class IesiRestApplicationTests {
//
////	@TestConfiguration
////	static class EmployeeServiceImplTestContextConfiguration {
////
////		@Bean
////		public FrameworkExecution frameworkExecution() {
//////			Context context = new Context();
//////			context.setName("restserver");
//////			context.setScope("");
//////			FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
//////
//////			FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
//////			String frameworkHome = "C:\\Users\\robbe.berrevoets\\git\\iesi\\core\\java\\iesi-rest\\src\\test\\resources\\iesi_instance";
//////			FrameworkFolderConfiguration frameworkFolderConfiguration = new FrameworkFolderConfiguration(frameworkHome);
//////			FrameworkSettingConfiguration frameworkSettingConfiguration = new FrameworkSettingConfiguration(frameworkHome);
//////			FrameworkActionTypeConfiguration frameworkActionTypeConfiguration = new FrameworkActionTypeConfiguration(frameworkFolderConfiguration);
//////			FrameworkGenerationRuleTypeConfiguration frameworkGenerationRuleTypeConfiguration = new FrameworkGenerationRuleTypeConfiguration(frameworkFolderConfiguration);
//////
//////			FrameworkConfiguration frameworkConfiguration = new FrameworkConfiguration(frameworkHome, frameworkFolderConfiguration,
//////					frameworkSettingConfiguration, frameworkActionTypeConfiguration, frameworkGenerationRuleTypeConfiguration);
//////
//////			return new FrameworkExecution(new FrameworkInstance("write", frameworkInitializationFile, frameworkConfiguration),
//////					frameworkExecutionContext, frameworkInitializationFile);
////
////			Context context = new Context();
////			context.setName("restserver");
////			context.setScope("");
////			FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
////			FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
////			frameworkInitializationFile.setName("");
////			return new FrameworkExecution(new FrameworkInstance(frameworkInitializationFile), frameworkExecutionContext, frameworkInitializationFile);
////
////		}
////
////		@Bean
////		public ConnectionConfiguration connectionConfiguration(FrameworkExecution frameworkExecution) {
////			return new ConnectionConfiguration(frameworkExecution);
////		}
////
////		@Bean
////		public EnvironmentConfiguration environmentConfiguration(FrameworkExecution frameworkExecution) {
////			return new EnvironmentConfiguration(frameworkExecution);
////		}
////
////		@Bean
////		public ImpersonationConfiguration impersonationConfiguration(FrameworkExecution frameworkExecution) {
////			return new ImpersonationConfiguration(frameworkExecution);
////		}
////
////		@Bean
////		public ScriptConfiguration scriptConfiguration(FrameworkExecution frameworkExecution) {
////			return new ScriptConfiguration(frameworkExecution);
////		}
////
////		@Bean
////		public UserConfiguration userConfiguration(FrameworkExecution frameworkExecution) {
////			return new UserConfiguration(frameworkExecution);
////		}
////
////		@Bean
////		public ComponentConfiguration componentConfiguration(FrameworkExecution frameworkExecution) {
////			return new ComponentConfiguration(frameworkExecution);
////		}
////	}
//
//
//	@MockBean
//	private ConnectionConfiguration connectionConfiguration;
//
//	@Autowired
//	private MockMvc mvc;
//
//	@Test
//	public void contextLoads() throws Exception {
//		Connection connection = new Connection("test", "db.test", "description", "environment",
//				Collections.singletonList(new ConnectionParameter("param1", "value1")));
//		List<Connection> components = Collections.singletonList(connection);
//
//		given(connectionConfiguration.getConnections()).willReturn(components);
//
//		mvc.perform(get("/api/connections"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$._embedded").isArray())
//				.andExpect(jsonPath("$._embedded", hasSize(1)))
//				.andExpect(jsonPath("$._embedded[0].name", is("test")))
//				.andExpect(jsonPath("$._embedded[0].type", is("db.test")));
//	}
//
//}
