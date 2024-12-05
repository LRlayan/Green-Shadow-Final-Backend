package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.impl.UserDTO;
import com.example.cropMonitorSystem.exception.UserNotFoundException;
import com.example.cropMonitorSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<UserDTO> getALlUsers(){
        try {
            List<UserDTO> user = userService.getALlUsers();
            logger.info("Successfully retrieved {} users.", user.size());
            return user;
        }catch (Exception e){
            logger.error("Error occurred while retrieving users.", e);
            throw e;
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("userId") String userId){
        try{
            userService.deleteUser(userId);
            logger.info("Successfully deleted user with ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (UserNotFoundException e){
            logger.error("User not found with ID: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting user with ID: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
