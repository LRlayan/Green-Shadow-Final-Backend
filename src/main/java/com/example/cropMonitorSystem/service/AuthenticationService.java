package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.impl.ChangePasswordDTO;
import com.example.cropMonitorSystem.secureAndResponse.response.JwtAuthResponse;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignIn;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignUp;

public interface AuthenticationService {

    JwtAuthResponse signUp(SignUp signUp);
    JwtAuthResponse signIn(SignIn signIn);

    JwtAuthResponse refreshToken(String refreshToken);
    void changePassword(ChangePasswordDTO changePasswordDTO);
}
