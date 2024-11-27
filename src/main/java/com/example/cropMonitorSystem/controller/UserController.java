package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.impl.ChangePasswordDTO;
import com.example.cropMonitorSystem.dto.impl.Token;
import com.example.cropMonitorSystem.dto.impl.UserWithKey;
import com.example.cropMonitorSystem.secureAndResponse.response.JwtAuthResponse;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignIn;
import com.example.cropMonitorSystem.secureAndResponse.secure.SignUp;
import com.example.cropMonitorSystem.service.AuthenticationService;
import com.example.cropMonitorSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<JwtAuthResponse> signup(@RequestBody SignUp signup){
        return ResponseEntity.ok(authenticationService.signUp(signup));
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody SignIn signIn){
        return ResponseEntity.ok(authenticationService.signIn(signIn));
    }

    @PostMapping(value = "/refresh",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody() Token token){
        System.out.println(token.getToken());
        return ResponseEntity.ok(authenticationService.refreshToken(token.getToken()));
    }


    @PostMapping(value = "/sendCode")
    public ResponseEntity<Void> sendCode(@RequestBody() UserWithKey userWithKey){
        if (userService.sendCodeToChangePassword(userWithKey)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/changePassword")
    public ResponseEntity<Void> changePassword(@RequestBody() ChangePasswordDTO changePasswordDTO){
        try {
            authenticationService.changePassword(changePasswordDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
