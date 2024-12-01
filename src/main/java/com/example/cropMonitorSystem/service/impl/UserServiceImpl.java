package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.UserDAO;
import com.example.cropMonitorSystem.dto.impl.UserDTO;
import com.example.cropMonitorSystem.service.UserService;
import com.example.cropMonitorSystem.util.Mapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final Mapping mapping;

    @Override
    public List<UserDTO> getALlUsers() {
        return mapping.userList(userDAO.findAll());
    }
}
