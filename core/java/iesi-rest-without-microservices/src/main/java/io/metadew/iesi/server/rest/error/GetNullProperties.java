package io.metadew.iesi.server.rest.error;

import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.resource.environment.dto.EnvironmentDto;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptActionDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptVersionDto;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class GetNullProperties {
	
		public void getNullEnvironment(@Valid EnvironmentDto environments) {

			if (environments.getDescription() == null || environments.getName() == null
					|| environments.getParameters().get(0).getName() == null || environments.getParameters().get(0).getValue() == null ) {
				throw new SqlNotFoundException();
			}
	}

	public void getNullImpersonation(@Valid ImpersonationDto impersonation){

			if(impersonation.getName() == null || impersonation.getDescription() == null || impersonation.getParameters().get(0).getConnection() == null ||
					impersonation.getParameters().get(0).getDescription() == null || impersonation.getParameters().get(0).getImpersonation() == null)
			{
				throw new SqlNotFoundException();
			}
	}

		public void getNullConnection(@Valid ConnectionDto connection){

			if(connection.getEnvironment() == null || connection.getName() == null || connection.getDescription() == null ||
			connection.getType() == null || connection.getParameters().get(0).getName() == null || connection.getParameters().get(0).getValue() == null )
			{
				throw new SqlNotFoundException();
			}
	}

		public void getNullComponent (@Valid ComponentDto component){
			List<ComponentDto> components = new ArrayList<>();
			List<List<ComponentAttribute>> attributes = components.stream().map(x -> x.getAttributes())
					.collect(Collectors.toList());
			if(component.getName() == null || component.getType() == null || component.getDescription() == null ||
			component.getVersion().getDescription() == null || component.getParameters().get(0).getName() == null || component.getParameters().get(0).getValue() == null ||
					attributes == null
			) {
					throw new SqlNotFoundException();
			}
		}

		public void getNullScript (@Valid ScriptDto script){
			List<ScriptDto> scripts = new ArrayList<>();
			List<List<ScriptActionDto>> action = scripts.stream().map(x -> x.getActions()).collect(Collectors.toList());
			List<ScriptVersionDto> version = scripts.stream().map(x -> x.getVersion()).collect(Collectors.toList());
			List<List<ScriptParameter>> parameters = scripts.stream().map(x -> x.getParameters())
					.collect(Collectors.toList());
			if (script.getName() == null || script.getDescription() == null
					|| script.getId() == null ||  version == null
					|| action == null || parameters == null) {
				throw new SqlNotFoundException();
			}
		}
}
