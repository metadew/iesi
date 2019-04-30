package io.metadew.iesi.server.rest.pagination;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.metadew.iesi.metadata.definition.Script;

@Repository
public class ScriptRepository {
	@SuppressWarnings("unused")
	private final List<Script> script;

	public ScriptRepository(List<Script> script) {
		super();
		this.script = script;
	}
	
	public List<Script> search(List<Script> script, ScriptCriteria criteria){
		if(criteria.getQuery()==null) {
			return script;
		}
		return script.stream().filter(x -> x.getName().contains(criteria.getQuery())).skip(criteria.getSkip())
				.limit(criteria.getLimit()).collect(toList());
	}
}