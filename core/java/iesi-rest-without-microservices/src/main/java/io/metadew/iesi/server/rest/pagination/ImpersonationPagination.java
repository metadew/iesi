package io.metadew.iesi.server.rest.pagination;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class ImpersonationPagination {

	public List<Impersonation> search(List<Impersonation> impersonation, ImpersonationCriteria impersonationCriteria) {
		if (impersonationCriteria.getName() != null) {

			return impersonation.stream().filter(p -> p.getName().contains(impersonationCriteria.getName()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());

		} else if (impersonationCriteria.getDescription() != null) {
			return impersonation.stream()
					.filter(p -> p.getDescription().contains(impersonationCriteria.getDescription()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());

		} else if (impersonationCriteria.getParametersConnection() != null && impersonation.get(0).getParameters().size() >= 1) {
			return impersonation.stream()
					.filter(p -> p.getParameters().get(0).getConnection()
							.contains(impersonationCriteria.getParametersConnection()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());
		
		} else if (impersonationCriteria.getParametersDescription()!= null && impersonation.get(0).getParameters().size() >= 1) {
			return impersonation.stream()
					.filter(p -> p.getParameters().get(0).getDescription()
							.contains(impersonationCriteria.getParametersDescription()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());
		
		} else if (impersonationCriteria.getParametersImpersonation() != null) {
			return impersonation.stream()
					.filter(p -> p.getParameters().get(0).getImpersonatedConnection()
							.contains(impersonationCriteria.getParametersImpersonation()))
					.skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit()).collect(toList());
		
		}
		return impersonation.stream().skip(impersonationCriteria.getSkip()).limit(impersonationCriteria.getLimit())
				.collect(toList());

	}
}