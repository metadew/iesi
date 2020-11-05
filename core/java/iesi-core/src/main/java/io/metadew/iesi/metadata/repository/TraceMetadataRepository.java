package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptLabelDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptVersionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptLabelTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptVersionTraceConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class TraceMetadataRepository extends MetadataRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    public TraceMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
        ScriptDesignTraceConfiguration.getInstance().init(this);
        ScriptVersionDesignTraceConfiguration.getInstance().init(this);
        ScriptParameterDesignTraceConfiguration.getInstance().init(this);
        ScriptLabelTraceConfiguration.getInstance().init(this);
//        ActionTraceConfiguration.getInstance().init(this);
        ScriptTraceConfiguration.getInstance().init(this);
        ScriptVersionTraceConfiguration.getInstance().init(this);
        ScriptParameterTraceConfiguration.getInstance().init(this);
        ScriptLabelDesignTraceConfiguration.getInstance().init(this);
        ActionDesignTraceConfiguration.getInstance().init(this);
        ActionParameterDesignTraceConfiguration.getInstance().init(this);
        ActionParameterTraceConfiguration.getInstance().init(this);
    }

    @Override
    public String getCategory() {
        return "trace";
    }


    @SuppressWarnings("unused")
    @Override
    public void save(DataObject dataObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("trace")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Trace repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
}
