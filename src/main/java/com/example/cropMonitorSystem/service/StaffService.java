package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.StaffStatus;
import com.example.cropMonitorSystem.dto.impl.StaffDTO;
import com.example.cropMonitorSystem.exception.StaffNotFoundException;

import java.util.List;

public interface StaffService {
    void saveStaffMember(StaffDTO staffDTO);
    List<StaffDTO> getAllStaffMember();
    void deleteStaffMember(String staffId) throws StaffNotFoundException;
    void updateStaffMember(String id, StaffDTO staffDTO);
    StaffStatus getSelectedStaffMember(String staffId);
}
