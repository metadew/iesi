package io.metadew.iesi.server.rest.client;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.user.AuthenticationRequest;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Log4j2
public class MasterClient implements IMasterClient {
    private static final String LOGIN_ENDPOINT = "/api/users/login";
    private static final String EXECUTION_REQUEST_ENDPOINT = "/api/execution-requests";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    private final WebClient webClient;

    public MasterClient(Configuration iesiProperties, WebClient.Builder webClientBuilder) {
        String iesiHost = (String) iesiProperties.getMandatoryProperty("iesi.master.host");
        this.webClient = webClientBuilder
                .baseUrl(iesiHost)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection().compress(true)))
                .build();
    }


    public Mono<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(LOGIN_ENDPOINT)
                        .build())
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .retrieve()
                .bodyToMono(AuthenticationResponse.class);
    }


    public Mono<ExecutionRequestDto> createExecutionRequest(ExecutionRequestPostDto executionRequestPostDto, String accessToken) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(EXECUTION_REQUEST_ENDPOINT)
                        .build())
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_PREFIX + accessToken)
                .body(Mono.just(executionRequestPostDto), ExecutionRequestPostDto.class)
                .retrieve()
                .bodyToMono(ExecutionRequestDto.class);
    }

}
