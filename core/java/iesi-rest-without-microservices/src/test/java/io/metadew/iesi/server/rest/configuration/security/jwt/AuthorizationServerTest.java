package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.script.ScriptsController;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
import io.metadew.iesi.server.rest.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, IesiConfiguration.class, AuthorizationServerConfiguration.class, ResourceServerConfiguration.class, JwtWebSecurityConfiguration.class, IesiUserDetailsManager.class,
        UserService.class, UserDtoRepository.class, FilterService.class, IesiUserAuthenticationConverter.class})
@ActiveProfiles({"test", "http"})
@WebMvcTest()
@AutoConfigureMockMvc
@DirtiesContext
public class AuthorizationServerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void beforeEach() {
        userService.addUser(new User(
                new UserKey(UUID.randomUUID()), "admin",
                bCryptPasswordEncoder.encode("admin"),
                true,
                false,
                false,
                false,
                new HashSet<>()

        ));
    }

    @AfterEach
    void afterEach() {
        userService.delete("admin");
    }

    TokenResponse getTokenSuccessfullyTest() throws Exception {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("username", "admin");
        parameters.add("password", "admin");
        parameters.add("grant_type", "password");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").exists())
                .andExpect(jsonPath("$.token_type", is("bearer")))
                .andExpect(jsonPath("$.expires_in").exists())
                .andExpect(jsonPath("$.scope").exists())
                .andExpect(jsonPath("$.scope", is("read-write")))
                .andExpect(jsonPath("$.jti").exists());

        String resultActionsString = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        Map<String, Object> responseMap = jsonParser.parseMap(resultActionsString);

        return new TokenResponse(responseMap.get("access_token").toString(), responseMap.get("refresh_token").toString());
    }

    @Test
    void getTokenWithBadClientId() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "teste");
        parameters.add("client_secret", "test");
        parameters.add("username", "admin");
        parameters.add("password", "admin");
        parameters.add("grant_type", "password");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_client")))
                .andExpect(jsonPath("$.error_description", is("Bad client credentials")));
    }

    @Test
    void getTokenWithBadClientSecret() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "teste");
        parameters.add("username", "admin");
        parameters.add("password", "admin");
        parameters.add("grant_type", "password");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_client")))
                .andExpect(jsonPath("$.error_description", is("Bad client credentials")));
    }

    @Test
    void getTokenWithUsernameNotFound() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("username", "admine");
        parameters.add("password", "admin");
        parameters.add("grant_type", "password");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_grant")))
                .andExpect(jsonPath("$.error_description", is("Bad credentials")));
    }

    @Test
    void getTokenWithWrongPassword() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("username", "admin");
        parameters.add("password", "admine");
        parameters.add("grant_type", "password");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_grant")))
                .andExpect(jsonPath("$.error_description", is("Bad credentials")));
    }

    @Test
    void getTokenWithUnavailableGrantType() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("authorization_code", "AE9SKQ");
        parameters.add("grant_type", "authorization_code");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_client")))
                .andExpect(jsonPath("$.error_description", is("Unauthorized grant type: authorization_code")));
    }

    @Test
    void getRefreshToken() throws Exception {
        TokenResponse tokenResponse = getTokenSuccessfullyTest();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("refresh_token", tokenResponse.getRefreshToken());
        parameters.add("grant_type", "refresh_token");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token", not(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token", not(tokenResponse.getRefreshToken())));
    }

    @Test
    void getRefreshTokenWithOldRefreshToken() throws Exception {
        TokenResponse tokenResponse = getTokenSuccessfullyTest();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", "test");
        parameters.add("client_secret", "test");
        parameters.add("refresh_token", tokenResponse.getRefreshToken());
        parameters.add("grant_type", "refresh_token");

        ResultActions resultActions = mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token", not(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token", not(tokenResponse.getRefreshToken())));

        mockMvc.perform(
                        post("/oauth/token")
                                .params(parameters)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_grant")))
                .andExpect(jsonPath("$.error_description", containsString("Invalid refresh token: ")));
    }
}
