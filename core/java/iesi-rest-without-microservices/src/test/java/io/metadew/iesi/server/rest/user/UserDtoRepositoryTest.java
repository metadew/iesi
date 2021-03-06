package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class UserDtoRepositoryTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private UserConfiguration userConfiguration;

    @Autowired
    private TeamConfiguration teamConfiguration;

    @Autowired
    private IUserDtoRepository userDtoRepository;

    @BeforeEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllPaginated() {
        Map<String, Object> teamInfo = TeamBuilder.generateTeam(1, 1, 2, new HashSet<>());
        teamConfiguration.insert((Team) teamInfo.get("team"));
        Team team = (Team) teamInfo.get("team");
        Map<String, Object> user1Info = UserBuilder.generateUser("user1", team.getRoles(), team.getTeamName());
        Map<String, Object> user2Info = UserBuilder.generateUser("user2", team.getRoles(), team.getTeamName());
        userConfiguration.insert((User) user1Info.get("user"));
        userConfiguration.insert((User) user2Info.get("user"));
        Pageable pageable = PageRequest.of(0, 3);

        assertThat(
                userDtoRepository.getAll(pageable, new HashSet<>()))
                .hasSize(2)
                .contains((UserDto) user1Info.get("userDto"), (UserDto) user2Info.get("userDto"));
    }

    @Test
    void getAllPaginatedFilterName() {
        Map<String, Object> teamInfo = TeamBuilder.generateTeam(1, 1, 2, new HashSet<>());
        teamConfiguration.insert((Team) teamInfo.get("team"));
        Team team = (Team) teamInfo.get("team");
        Map<String, Object> user1Info = UserBuilder.generateUser("filter", team.getRoles(), team.getTeamName());
        Map<String, Object> user2Info = UserBuilder.generateUser("user", team.getRoles(), team.getTeamName());
        userConfiguration.insert((User) user1Info.get("user"));
        userConfiguration.insert((User) user2Info.get("user"));
        Pageable pageable = PageRequest.of(0, 2);

        assertThat(
                userDtoRepository.getAll(pageable, new UserFiltersBuilder().username("filter").build()))
                .hasSize(1)
                .contains((UserDto) user1Info.get("userDto"));
    }

}
