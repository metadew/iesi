package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Primary
    @Order(0)
    @DependsOn("frameworkInstance")
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        MetadataRepositoryConfiguration.getInstance().getMetadataRepositories().forEach(MetadataRepository::createAllTables);
        return MetadataRepositoryConfiguration.getInstance();
    }
    private String getScriptAndScriptVRSTable(Pageable pageable, String scriptName, Long scriptVersion, boolean isLatestVersionOnly) {
      return (" (" +
                "SELECT " +
                "script.SCRIPT_ID, script.SCRIPT_NM, script.SCRIPT_DSC, " +
                "script_version.SCRIPT_VRS_NB, script_version.SCRIPT_VRS_DSC " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Scripts").getName() + " script " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() + " script_version " +
                "on script.SCRIPT_ID = script_version.SCRIPT_ID " +
                limitAndOffset(pageable) +
                ") ");
    }
    public String limitAndOffset(Pageable pageable) {
        String getOracleDb = io.metadew.iesi.common.configuration.Configuration.getInstance().getMandatoryProperty("iesi.metadata.repository.coordinator.type").toString();
        if (getOracleDb.equals("oracle")) {
            return pageable == null || pageable.isUnpaged() ? " " : " OFFSET " + pageable.getOffset() + " ROWS FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY ";
        }
        return pageable == null || pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";

    }

    @Test
    void getAllNoResultTest() throws Exception {
        // Mock Service
        Pageable pageable = PageRequest.of(0, 20);
       System.out.println(getScriptAndScriptVRSTable(pageable, "x", (long) 10,false));
    }

}