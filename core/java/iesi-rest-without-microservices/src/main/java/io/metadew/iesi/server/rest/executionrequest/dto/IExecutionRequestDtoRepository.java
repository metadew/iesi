package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;

import java.util.List;

public interface IExecutionRequestDtoRepository {
    List<ExecutionRequest> getAll(int limit, int pageNumber, List<String> column, List<String> sort, String filterColumn, String searchParam, String startDate, String endDate);
}