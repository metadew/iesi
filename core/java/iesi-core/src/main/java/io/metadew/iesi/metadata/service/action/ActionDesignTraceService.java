package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.metadata.configuration.action.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.execution.MetadataControl;

import java.sql.SQLException;

public class ActionDesignTraceService {


    private ActionDesignTraceConfiguration actionDesignTraceConfiguration;
    private ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;


    public ActionDesignTraceService(MetadataControl metadataControl) {
        this.actionDesignTraceConfiguration = new ActionDesignTraceConfiguration(metadataControl);
        this.actionParameterDesignTraceConfiguration = new ActionParameterDesignTraceConfiguration(metadataControl);
    }

    public void trace(String runId, Long processId, Action action) {
        try {
            actionDesignTraceConfiguration.insert(new ActionDesignTrace(runId, processId, action));
            for (ActionParameter actionParameter : action.getParameters()) {
                actionParameterDesignTraceConfiguration.insert(new ActionParameterDesignTrace(runId, processId, action.getId(), actionParameter));
            }
        } catch (MetadataAlreadyExistsException | SQLException e) {
            e.printStackTrace();
        }
    }
}
