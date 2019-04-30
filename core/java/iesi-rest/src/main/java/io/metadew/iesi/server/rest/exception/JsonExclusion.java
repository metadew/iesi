package io.metadew.iesi.server.rest.exception;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import io.metadew.iesi.metadata.definition.Connection;

public class JsonExclusion implements ExclusionStrategy {

	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes x) {

		return (x.getDeclaringClass() == Connection.class && x.getName().equals("parameters"))
				|| (x.getDeclaringClass() == Connection.class && x.getName().equals("environment"));
	}

}