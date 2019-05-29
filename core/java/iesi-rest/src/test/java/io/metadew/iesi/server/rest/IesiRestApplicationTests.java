package io.metadew.iesi.server.rest;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.framework.configuration.*;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IesiRestApplicationTests {

	@TestConfiguration
	static class EmployeeServiceImplTestContextConfiguration {

		@Bean
		public FrameworkInstance frameworkInstance() {
			Context context = new Context();
			context.setName("restserver");
			context.setScope("");
			FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
			FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
			String frameworkHome = "C:\\Users\\robbe.berrevoets\\git\\iesi\\core\\java\\iesi-rest\\src\\test\\resources\\iesi_instance";
			FrameworkFolderConfiguration frameworkFolderConfiguration = new FrameworkFolderConfiguration(frameworkHome);
			FrameworkSettingConfiguration frameworkSettingConfiguration = new FrameworkSettingConfiguration(frameworkHome);
			FrameworkActionTypeConfiguration frameworkActionTypeConfiguration = new FrameworkActionTypeConfiguration(frameworkFolderConfiguration);
			FrameworkGenerationRuleTypeConfiguration frameworkGenerationRuleTypeConfiguration = new FrameworkGenerationRuleTypeConfiguration(frameworkFolderConfiguration);

			FrameworkConfiguration frameworkConfiguration = new FrameworkConfiguration(frameworkHome, frameworkFolderConfiguration,
					frameworkSettingConfiguration, frameworkActionTypeConfiguration, frameworkGenerationRuleTypeConfiguration);

			return new FrameworkInstance("write", frameworkInitializationFile, frameworkConfiguration);
		}
	}

	@Test
	public void contextLoads() {



	}

}
