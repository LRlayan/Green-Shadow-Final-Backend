package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.EquipmentStatus;
import com.example.cropMonitorSystem.dto.impl.EquipmentDTO;

import java.util.List;

public interface EquipmentService {
    void saveEquipment(EquipmentDTO equipmentDTO);
    List<EquipmentDTO> getAllEquipment();
    void deleteEquipment(String id);
    void updateEquipment(String id,EquipmentDTO equipmentDTO);
    EquipmentStatus getSelectedEquipment(String equipmentId);
}
