package io.metadew.iesi.server.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@ConditionalOnWebApplication
public class UserDtoService implements IUserDtoService {

    private final UserDtoRepository userDtoRepository;

    @Autowired
    public UserDtoService(UserDtoRepository userDtoRepository) {
        this.userDtoRepository = userDtoRepository;
    }


    public Optional<UserDto> get(String username) {
        return userDtoRepository.get(username);
    }

    public Optional<UserDto> get(UUID uuid) {
        return userDtoRepository.get(uuid);
    }

    public Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters) {
        return userDtoRepository.getAll(pageable, userFilters);
    }

}
