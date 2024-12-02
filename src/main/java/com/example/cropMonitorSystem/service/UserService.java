package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.impl.UserDTO;
import com.example.cropMonitorSystem.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface UserService {
    List<UserDTO> getALlUsers();
    void deleteUser(String userId) throws UserNotFoundException;
}
