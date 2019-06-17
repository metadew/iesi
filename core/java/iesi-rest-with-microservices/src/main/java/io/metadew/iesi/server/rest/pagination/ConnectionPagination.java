package io.metadew.iesi.server.rest.pagination;

import static java.util.stream.Collectors.toList;

import java.util.List;

import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import org.springframework.stereotype.Repository;


@Repository
public class ConnectionPagination {

	public List<Connection> search(List<Connection> connection, ConnectionCriteria connectionCriteria) {
		if (connectionCriteria.getName() != null) {

			return connection.stream().filter(p -> p.getName().contains(connectionCriteria.getName()))
					.skip(connectionCriteria.getSkip()).limit(connectionCriteria.getLimit()).collect(toList());

		} else if (connectionCriteria.getDescription() != null) {
			return connection.stream().filter(p -> p.getDescription().contains(connectionCriteria.getDescription()))
					.skip(connectionCriteria.getSkip()).limit(connectionCriteria.getLimit()).collect(toList());

		} else if (connectionCriteria.getType() != null) {
			return connection.stream().filter(p -> p.getType().contains(connectionCriteria.getType()))
					.skip(connectionCriteria.getSkip()).limit(connectionCriteria.getLimit()).collect(toList());
		}

		return connection.stream().skip(connectionCriteria.getSkip()).limit(connectionCriteria.getLimit())
				.collect(toList());

	}
}
