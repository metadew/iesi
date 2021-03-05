package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDtoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserDtoModelAssembler extends RepresentationModelAssemblerSupport<User, UserDto> {


    @Autowired
    public UserDtoModelAssembler(DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler) {
        super(UserController.class, UserDto.class);
    }

    @Override
    public UserDto toModel(User user) {
        throw new UnsupportedOperationException();
    }

    public UserDto toModel(UserDto userDto) {
        return userDto;
    }

}