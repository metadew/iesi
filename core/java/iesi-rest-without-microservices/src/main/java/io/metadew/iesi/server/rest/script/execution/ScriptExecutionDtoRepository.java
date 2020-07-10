package io.metadew.iesi.server.rest.script.execution;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;

//@Log4j2
@Repository
public class ScriptExecutionDtoRepository implements IScriptExecutionDtoRepository {

    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    ScriptExecutionDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration){
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public ScriptExecutionDto getByRunIdAndProcessId(String runId, String processId) {
        ScriptExecutionDto scriptExecutionDto;
        // Todo: Create the SQL Query
        String SQLQuery = "";

        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getResultMetadataRepository()
                .executeQuery(SQLQuery, "reader");

        // Todo: Create a row mapper

        // Todo: return the created Object
        return null;
    }
}
