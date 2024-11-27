package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.impl.UserWithKey;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();

    boolean sendCodeToChangePassword(UserWithKey userWithKey);
}
