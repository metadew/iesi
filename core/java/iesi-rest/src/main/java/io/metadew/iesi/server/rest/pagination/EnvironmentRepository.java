package io.metadew.iesi.server.rest.pagination;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.metadew.iesi.metadata.definition.Environment;

@Repository
public class EnvironmentRepository {

	public List<Environment> search(List<Environment> environment, EnvironmentCriteria environmentCriteria) {
		if (environmentCriteria.getName() != null) {

			return environment.stream().filter(p -> p.getName().contains(environmentCriteria.getName()))
					.skip(environmentCriteria.getSkip()).limit(environmentCriteria.getLimit()).collect(toList());

		} else if (environmentCriteria.getDescription() != null) {
			return environment.stream().filter(p -> p.getDescription().contains(environmentCriteria.getDescription()))
					.skip(environmentCriteria.getSkip()).limit(environmentCriteria.getLimit()).collect(toList());

		} else if (environmentCriteria.getParametersName() != null) {
			return environment.stream()
					.filter(p -> p.getParameters().get(0).getName().contains(environmentCriteria.getParametersName()))
					.skip(environmentCriteria.getSkip()).limit(environmentCriteria.getLimit()).collect(toList());

		} else if (environmentCriteria.getParametersValue() != null) {
			return environment.stream()
					.filter(p -> p.getParameters().get(0).getValue().contains(environmentCriteria.getParametersValue()))
					.skip(environmentCriteria.getSkip()).limit(environmentCriteria.getLimit()).collect(toList());

		}

		return environment.stream().skip(environmentCriteria.getSkip()).limit(environmentCriteria.getLimit())
				.collect(toList());

	}

}
