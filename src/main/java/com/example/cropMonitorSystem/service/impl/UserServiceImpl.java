package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.UserDAO;
import com.example.cropMonitorSystem.dto.impl.UserWithKey;
import com.example.cropMonitorSystem.entity.impl.UserEntity;
import com.example.cropMonitorSystem.service.EmailService;
import com.example.cropMonitorSystem.service.UserService;
import com.example.cropMonitorSystem.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final EmailService emailService;

    @Override
    public UserDetailsService userDetailsService() {
        return username ->
                userDAO.findByEmail(username).
                        orElseThrow(()->new UsernameNotFoundException("User Not Found"));

    }

    @Override
    public boolean sendCodeToChangePassword(UserWithKey userWithKey) {
        Optional<UserEntity> byEmail = userDAO.findByEmail(userWithKey.getEmail());
        if (byEmail.isPresent()){
            emailService.sendEmail(userWithKey.getEmail(),"Your password change Code From Green Shadow(PVT) Ltd.","Dont share with anyone:  "+userWithKey.getCode());
            return true;
        }
        return false;
    }

    @Override
    public String getOTP() {
        return AppUtil.getOTP();
    }
}
