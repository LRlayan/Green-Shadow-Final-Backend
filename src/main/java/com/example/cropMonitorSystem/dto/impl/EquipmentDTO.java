package com.example.cropMonitorSystem.dto.impl;

import com.example.cropMonitorSystem.dto.EquipmentStatus;
import com.example.cropMonitorSystem.dto.SuperDTO;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EquipmentDTO implements SuperDTO, EquipmentStatus {
    @Id
    private String equipmentCode;
    private String name;
    private String type;
    private String status;
    private int availableCount;
    private List<String> staffCodeList;
    private List<String> fieldList;
}
