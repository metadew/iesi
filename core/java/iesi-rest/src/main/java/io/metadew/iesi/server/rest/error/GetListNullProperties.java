package io.metadew.iesi.server.rest.error;

import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.server.rest.ressource.component.ComponentPostByNameDto;
import io.metadew.iesi.server.rest.ressource.environment.EnvironmentDto;
import io.metadew.iesi.server.rest.ressource.impersonation.ImpersonationDto;
import io.metadew.iesi.server.rest.ressource.script.ScriptDto;
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
			if (environments.get(index).getDescription() == null || environments.get(index).getName() == null
					|| parameters == null) {
				throw new SqlNotFoundException();
			}

		}
	}

	public void getNullConnection(@Valid List<Connection> connections) {
		for (int index = 0; index < connections.size(); index++) {
			List<List<ConnectionParameter>> parameters = connections.stream().map(x -> x.getParameters())
					.collect(Collectors.toList());
			if (connections.get(index).getDescription() == null || connections.get(index).getEnvironment() == null
					|| connections.get(index).getName() == null || parameters == null
					|| connections.get(index).getType() == null) {
				throw new SqlNotFoundException();
			}
		}
	}

	public void getNullComponent(@Valid List<ComponentPostByNameDto> components) {
		for (int index = 0; index < components.size(); index++) {
			List<List<ComponentParameter>> parameters = components.stream().map(x -> x.getParameters())
					.collect(Collectors.toList());
			List<List<ComponentAttribute>> attributes = components.stream().map(x -> x.getAttributes())
					.collect(Collectors.toList());
			if (components.get(index).getDescription() == null || components.get(index).getId() == null
					|| components.get(index).getName() == null || parameters == null || attributes == null
				) {
				throw new SqlNotFoundException();
			}
		}
	}

	public void getNullImpersonation(@Valid List<ImpersonationDto> impersonations) {
		for (int index = 0; index < impersonations.size(); index++) {
			List<List<ImpersonationParameter>> parameters = impersonations.stream().map(x -> x.getParameters())
					.collect(Collectors.toList());
			if (impersonations.get(index).getDescription() == null || impersonations.get(index).getName() == null
					|| parameters == null) {
				throw new SqlNotFoundException();
			}
		}
	}

	public void getNullScript(@Valid List<ScriptDto> scripts) {
		for (int index = 0; index < scripts.size(); index++) {
			List<List<Action>> action = scripts.stream().map(x -> x.getActions()).collect(Collectors.toList());
			List<ScriptVersion> version = scripts.stream().map(x -> x.getVersion()).collect(Collectors.toList());
			List<List<ScriptParameter>> parameters = scripts.stream().map(x -> x.getParameters())
					.collect(Collectors.toList());
			if (scripts.get(index).getName() == null || scripts.get(index).getDescription() == null
					|| scripts.get(index).getId() == null ||  version == null
					|| action == null || parameters == null) {
				throw new SqlNotFoundException();
			}
		}
	}

}