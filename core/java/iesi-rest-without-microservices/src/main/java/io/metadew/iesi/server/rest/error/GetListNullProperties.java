package io.metadew.iesi.server.rest.error;

import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.resource.environment.dto.EnvironmentDto;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationParameterDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptDto;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GetListNullProperties {

    public void getNullEnvironment(@Valid List<EnvironmentDto> environments) {
        for (int index = 0; index < environments.size(); index++) {
            List<List<EnvironmentParameter>> parameters = environments.stream().map(x -> x.getParameters())
                    .collect(Collectors.toList());
            if ( environments.get(index).getName() == null
                    || parameters == null) {
                throw new SqlNotFoundException();
            }

        }
    }

    public void getNullConnection(@Valid List<ConnectionDto> connections) {
        for (int index = 0; index < connections.size(); index++) {
            List<List<ConnectionParameter>> parameters = connections.stream().map(ConnectionDto::getParameters)
                    .collect(Collectors.toList());
            if (connections.get(index).getEnvironment() == null
                    || connections.get(index).getName() == null || parameters == null
                    || connections.get(index).getType() == null) {
                throw new SqlNotFoundException();
            }
        }
    }

    public void getNullComponent(@Valid List<ComponentDto> components) {
        for (int index = 0; index < components.size(); index++) {
            List<List<ComponentParameter>> parameters = components.stream().map(x -> x.getParameters())
                    .collect(Collectors.toList());
            List<List<ComponentAttribute>> attributes = components.stream().map(x -> x.getAttributes())
                    .collect(Collectors.toList());
            if (components.get(index).getVersion().getDescription() == null
                    || components.get(index).getName() == null || parameters == null || attributes == null
            ) {
                throw new SqlNotFoundException();
            }
        }
    }

    public void getNullImpersonation(@Valid List<ImpersonationDto> impersonations) {
        for (int index = 0; index < impersonations.size(); index++) {
            List<List<ImpersonationParameterDto>> parameters = impersonations.stream().map(x -> x.getParameters())
                    .collect(Collectors.toList());
            if (impersonations.get(index).getName() == null || parameters.get(0).get(0).getImpersonation()  == null ||
                    parameters.get(0).get(0).getImpersonation()  == null
                  ) {
                throw new SqlNotFoundException();
            }
        }
    }

    public void getNullScript(@Valid List<ScriptDto> scripts) {
        for (int index = 0; index < scripts.size(); index++) {
            if (scripts.get(index).getName() == null || scripts.get(index).getDescription() == null

            ) {
                throw new SqlNotFoundException();
            }
        }
    }

}