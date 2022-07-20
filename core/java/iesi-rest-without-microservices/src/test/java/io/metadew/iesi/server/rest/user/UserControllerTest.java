package io.metadew.iesi.server.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.security_group.*;
import io.metadew.iesi.server.rest.user.team.TeamDtoRepository;
import io.metadew.iesi.server.rest.user.team.TeamPutDtoService;
import io.metadew.iesi.server.rest.user.team.TeamsController;
import io.metadew.iesi.server.rest.user.team.dto.TeamDtoResourceAssembler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class, TeamsController.class, SecurityGroupController.class, CustomGlobalExceptionHandler.class,
        TeamService.class, IUserService.class, UserDtoModelAssembler.class, SecurityGroupService.class, SecurityGroupDtoRepository.class, SecurityGroupPutDtoService.class,
        SecurityGroupDtoResourceAssembler.class, io.metadew.iesi.server.rest.user.team.TeamService.class, TeamDtoRepository.class, TeamPutDtoService.class, TeamDtoResourceAssembler.class,
        BCryptPasswordEncoder.class, UserDtoRepository.class, UserService.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class,
        UserDtoRepository.class, FilterService.class})
@ActiveProfiles("test")
@DirtiesContext
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPostDto userPostDto = new UserPostDto(
                "user1",
                "password",
                "password"
        );
        UserDto user = new UserDto(
                UUID.randomUUID(),
                "user1",
                true,
                false,
                false,
                false,
                new HashSet<>()
        );

        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        when(userService.exists("user1")).thenReturn(false);
        when(userService.get((UUID) any())).thenReturn(Optional.of(user));

        mvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user1")));
    }

    @Test
    void createAlreadyExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPostDto userPostDto = new UserPostDto(
                "user1",
                "password",
                "password"
        );

        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        when(userService.exists("user1")).thenReturn(true);

        mvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPasswordsMisMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPostDto userPostDto = new UserPostDto(
                "user1",
                "password",
                "password"
        );
        UserDto user = new UserDto(
                UUID.randomUUID(),
                "user1",
                true,
                false,
                false,
                false,
                new HashSet<>()
        );

        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        mvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("400")))
                .andExpect(jsonPath("$.message", is("The provided passwords do not match each other")));
    }

    @Test
    void update() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UUID userUuid = UUID.randomUUID();
        UserPostDto userPostDto = new UserPostDto(
                "user2",
                "password",
                "password"
        );

        User user = new User(
                new UserKey(userUuid),
                "user1",
                "my-password",
                true,
                false,
                false,
                false,
                new HashSet<>()
        );

        UserDto userDto = new UserDto(
                userUuid,
                "user2",
                true,
                false,
                false,
                false,
                new HashSet<>()
        );


        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        when(userService.getRawUser(new UserKey(userUuid))).thenReturn(Optional.of(user));
        when(userService.exists("user1")).thenReturn(false);
        when(userService.get(userUuid)).thenReturn(Optional.of(userDto));

        mvc.perform(
                        put("/users/" + userUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.username", is("user2")));
    }

    @Test
    void updateUuidNotExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UUID userUuid = UUID.randomUUID();
        UserPostDto userPostDto = new UserPostDto(
                "user2",
                "password",
                "password"
        );

        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        when(userService.getRawUser(new UserKey(userUuid))).thenReturn(Optional.empty());

        mvc.perform(
                        put("/users/" + userUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.errorCode", is("404")))
                .andExpect(jsonPath("$.message", is("The user with the id \"" + userUuid + "\" does not exist")));
    }

    @Test
    void updatePasswordsMisMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UUID userUuid = UUID.randomUUID();
        UserPostDto userPostDto = new UserPostDto(
                "user2",
                "password",
                "password"
        );

        String userPostDtoString = objectMapper.writeValueAsString(userPostDto);
        mvc.perform(
                        put("/users/" + userUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.errorCode", is("400")))
                .andExpect(jsonPath("$.message", is("The provided passwords do not match each other")));
    }

}
