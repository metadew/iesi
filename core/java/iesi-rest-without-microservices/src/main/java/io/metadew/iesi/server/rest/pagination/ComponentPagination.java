package io.metadew.iesi.server.rest.pagination;

import io.metadew.iesi.metadata.definition.component.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class ComponentPagination {
	
	public List<Component> search(List<Component> component, ComponentCriteria componentCriteria) {
		if (componentCriteria.getName() != null) {

			return component.stream().filter(p -> p.getName().contains(componentCriteria.getName()))
					.skip(componentCriteria.getSkip()).limit(componentCriteria.getLimit()).collect(toList());

		} else if (componentCriteria.getDescription() != null) {
			return component.stream().filter(p -> p.getDescription().contains(componentCriteria.getDescription()))
					.skip(componentCriteria.getSkip()).limit(componentCriteria.getLimit()).collect(toList());

		} else if (componentCriteria.getType() != null) {
			return component.stream().filter(p -> p.getType().contains(componentCriteria.getType()))
					.skip(componentCriteria.getSkip()).limit(componentCriteria.getLimit()).collect(toList());
		}

		return component.stream().skip(componentCriteria.getSkip()).limit(componentCriteria.getLimit())
				.collect(toList());

	}
}