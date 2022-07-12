package io.metadew.iesi.server.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({ UserController.class, TeamsController.class, SecurityGroupController.class })
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class, TeamsController.class, SecurityGroupController.class, CustomGlobalExceptionHandler.class, PasswordEncoder.class,
        TeamService.class, IUserService.class, UserDtoModelAssembler.class, SecurityGroupService.class, SecurityGroupDtoRepository.class, SecurityGroupPutDtoService.class,
        SecurityGroupDtoResourceAssembler.class, io.metadew.iesi.server.rest.user.team.TeamService.class, TeamDtoRepository.class, TeamPutDtoService.class, TeamDtoResourceAssembler.class,
        BCryptPasswordEncoder.class,
        UserDtoRepository.class, UserService.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class,
        UserDtoRepository.class, FilterService.class})
@ActiveProfiles("test")
@DirtiesContext
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPostDto userPostDto = new UserPostDto(
                "user1",
                "password1",
                "password1"
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
                "password1",
                "password1"
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
        when(userService.exists("user1")).thenReturn(true);

        mvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userPostDtoString))
                .andExpect(status().isBadRequest());
    }

}
