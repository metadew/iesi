package io.metadew.iesi.server.rest.pagination;

import io.metadew.iesi.metadata.definition.script.Script;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class ScriptPagination {

	public List<Script> search(List<Script> script, ScriptCriteria scriptCriteria) {
		if (scriptCriteria.getName() != null) {

			return script.stream().filter(p -> p.getName().contains(scriptCriteria.getName()))
					.skip(scriptCriteria.getSkip()).limit(scriptCriteria.getLimit()).collect(toList());

		} else if (scriptCriteria.getDescription() != null) {
			return script.stream().filter(p -> p.getDescription().contains(scriptCriteria.getDescription()))
					.skip(scriptCriteria.getSkip()).limit(scriptCriteria.getLimit()).collect(toList());

		}
		return script.stream().skip(scriptCriteria.getSkip()).limit(scriptCriteria.getLimit())
				.collect(toList());

	}
}