package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.UserDAO;
import com.example.cropMonitorSystem.dto.impl.ChangePasswordDTO;
import com.example.cropMonitorSystem.dto.impl.UserDTO;
import com.example.cropMonitorSystem.entity.impl.UserEntity;
import com.example.cropMonitorSystem.secureAndResponse.response.JwtAuthResponse;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignIn;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignUp;
import com.example.cropMonitorSystem.service.AuthenticationService;
import com.example.cropMonitorSystem.service.EmailService;
import com.example.cropMonitorSystem.service.JwtService;
import com.example.cropMonitorSystem.util.Mapping;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Mapping mapping;
    private final UserDAO userDAO;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public JwtAuthResponse signUp(SignUp signUp) {
        int number = 0;
        UserEntity user = userDAO.findLastRowNative();
        if (user != null){
            String[] parts = user.getUser_id().split("-");
            number = Integer.parseInt(parts[1]);
        }
        emailService.sendEmail(signUp.getEmail(),"Email From GreenShadow", "Your user email:  "+signUp.getEmail() +"\n Your temporary password to login: "+signUp.getPassword());
        UserDTO userDTO = UserDTO.builder()
                .user_id("USER-" + ++number)
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .role(signUp.getRole())
                .build();
        UserEntity save = userDAO.save(mapping.toUserEntity(userDTO));
        String generateToken = jwtService.generateToken(save);
        return JwtAuthResponse.builder().token(generateToken).build();
    }

    @Override
    public JwtAuthResponse signIn(SignIn signIn) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signIn.getEmail(), signIn.getPassword())
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        UserEntity user = userDAO.findByEmail(signIn.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var generateToken = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(generateToken).build();
    }

    @Override
    public JwtAuthResponse refreshToken(String accessToken) {
        //extract username
        var username = jwtService.extractUserName(accessToken);
        //check the user availability in the database
        var findUser = userDAO.findByEmail(username).orElseThrow();
        var refreshToken = jwtService.refreshToken(findUser);
        return JwtAuthResponse.builder().token(refreshToken).build();
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Optional<UserEntity> byEmail = userDAO.findByEmail(changePasswordDTO.getEmail());
        if (byEmail.isPresent()){
            UserEntity referenceById = userDAO.getReferenceById(byEmail.get().getUser_id());
            referenceById.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userDAO.save(referenceById);
        }
    }
}
