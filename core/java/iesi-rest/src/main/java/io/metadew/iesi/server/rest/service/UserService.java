package io.metadew.iesi.server.rest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.metadew.iesi.server.rest.models.User;

public interface UserService {
	 public Page<User> findAllUser(Pageable pageable);
}
