package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.StaffDAO;
import com.example.cropMonitorSystem.dao.UserDAO;
import com.example.cropMonitorSystem.dto.impl.UserDTO;
import com.example.cropMonitorSystem.entity.Role;
import com.example.cropMonitorSystem.entity.impl.StaffEntity;
import com.example.cropMonitorSystem.entity.impl.UserEntity;
import com.example.cropMonitorSystem.exception.UserNotFoundException;
import com.example.cropMonitorSystem.service.UserService;
import com.example.cropMonitorSystem.util.Mapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final StaffDAO staffDAO;
    private final Mapping mapping;

    @Override
    public List<UserDTO> getALlUsers() {
        return mapping.userList(userDAO.findAll());
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        StaffEntity staffEntity = staffDAO.getReferenceById(userId);
        if (staffEntity.getRole() == Role.MANAGER || staffEntity.getRole() == Role.ADMINISTRATIVE || staffEntity.getRole() == Role.SCIENTIST){
            Optional<UserEntity> userEntity = userDAO.findByEmail(staffEntity.getEmail());
            if (userEntity.isPresent()){
                userDAO.deleteById(userEntity.get().getUser_id());
            }else {
                throw  new UserNotFoundException("User Id with" + userEntity.get().getUser_id() + "Not found");
            }
        }
    }
}
