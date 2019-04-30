package io.metadew.iesi.server.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.metadew.iesi.server.rest.models.User;
import io.metadew.iesi.server.rest.repository.UserRepository;

@Service
public class DefaultService implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public Page<User> findAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public UserRepository getUser() {
        return userRepository;
    }
}