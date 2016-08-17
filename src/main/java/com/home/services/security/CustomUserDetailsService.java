package com.home.services.security;

import com.home.services.domain.User;
import com.home.services.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by vitaliy on 7/26/16.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("GET USER: " + email);
        User user = userService.getUserByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException(String.format("User with email= %s was not found", email)));
        CurrentUser u =  new CurrentUser(user);
        System.out.println("GET USER 2: " + u.getUser());
        return new CurrentUser(user);
    }
}
