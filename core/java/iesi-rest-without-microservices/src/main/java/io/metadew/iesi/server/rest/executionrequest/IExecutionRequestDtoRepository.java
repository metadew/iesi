package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.filter.ExecutionRequestFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IExecutionRequestDtoRepository extends PagingAndSortingRepository<ExecutionRequestDto, UUID> {

    Page<ExecutionRequestDto> getAll(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters);

    Optional<ExecutionRequestDto> getById(UUID uuid);

}
