package com.home.services.service;

import com.home.services.domain.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by vitaliy on 7/26/16.
 */
public interface UserService {

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Collection<User> getAllUsers();
}
