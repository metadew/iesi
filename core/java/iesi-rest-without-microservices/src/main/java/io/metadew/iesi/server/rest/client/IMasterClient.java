package io.metadew.iesi.server.rest.client;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.user.AuthenticationRequest;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import reactor.core.publisher.Mono;

public interface IMasterClient {

    Mono<AuthenticationResponse> login(AuthenticationRequest authenticationRequest);

    Mono<ExecutionRequestDto> createExecutionRequest(ExecutionRequestPostDto executionRequestPostDto, String accessToken);

}
