package io.metadew.iesi.server.rest.pagination;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.metadew.iesi.metadata.definition.Impersonation;

@Repository
public class ImpersonationRepository {

	public List<Impersonation> search(List<Impersonation> impersonation, ImpersonationCriteria impersonationCriteria) {
		if (impersonationCriteria.getName() != null) {

			return impersonation.stream().filter(p -> p.getName().contains(impersonationCriteria.getName()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());

		} else if (impersonationCriteria.getDescription() != null) {
			return impersonation.stream()
					.filter(p -> p.getDescription().contains(impersonationCriteria.getDescription()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());

		}
//			else if (impersonationCriteria.getParametersName() != null) {
//			return impersonation.stream()
//					.filter(p -> p.getParameters().get(0).contains(impersonationCriteria.getParametersName()))
//					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());
//		}

		return impersonation.stream().skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit())
				.collect(toList());

	}
}