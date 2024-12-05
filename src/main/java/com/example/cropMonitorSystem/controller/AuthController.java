package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.impl.ChangePasswordDTO;
import com.example.cropMonitorSystem.dto.impl.Token;
import com.example.cropMonitorSystem.dto.impl.UserWithKey;
import com.example.cropMonitorSystem.secureAndResponse.response.JwtAuthResponse;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignIn;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignUp;
import com.example.cropMonitorSystem.service.AuthenticationService;
import com.example.cropMonitorSystem.service.AuthService;
import com.example.cropMonitorSystem.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthenticationService authenticationService;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signUp")
    public ResponseEntity<JwtAuthResponse> signup(@RequestBody SignUp signup){
        SignUp signUp = new SignUp();
        signUp.setEmail(signUp.getEmail());
        signUp.setRole(signUp.getRole());
        signUp.setPassword(AppUtil.temporaryUserPasswordGenerator());
        logger.info("SignUp saved successfully with email: {}", signUp.getEmail());
        return ResponseEntity.ok(authenticationService.signUp(signup));
    }

    @PostMapping(value = "/signIn",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody SignIn signIn){
        return ResponseEntity.ok(authenticationService.signIn(signIn));
    }

    @PostMapping(value = "/refresh",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody() Token token){
        return ResponseEntity.ok(authenticationService.refreshToken(token.getToken()));
    }

    @PostMapping(value = "/sendCode")
    public ResponseEntity<Void> sendCode(@RequestBody() UserWithKey userWithKey){
        if (authService.sendCodeToChangePassword(userWithKey)) {
            logger.info("send code successfully: {}", userWithKey);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }else {
            logger.error("Error persisting code for forgetPassword: {}" , userWithKey);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getOTP")
    public String getOTP(){
        return authService.getOTP();
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE','SCIENTIST')")
    @PostMapping(value = "/changePassword")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        try {
            authenticationService.changePassword(changePasswordDTO);
            logger.info("Change password successfully: {}", changePasswordDTO.getEmail());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            logger.error("Unexpected error occurred while saving password {}", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
