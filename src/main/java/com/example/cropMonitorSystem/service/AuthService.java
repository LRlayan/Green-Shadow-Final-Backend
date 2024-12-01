package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.impl.UserWithKey;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    UserDetailsService userDetailsService();
    boolean sendCodeToChangePassword(UserWithKey userWithKey);
    String getOTP();
}
