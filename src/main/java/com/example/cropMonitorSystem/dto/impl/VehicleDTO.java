package com.example.cropMonitorSystem.dto.impl;

import com.example.cropMonitorSystem.dto.SuperDTO;
import com.example.cropMonitorSystem.dto.VehicleStatus;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTO implements SuperDTO , VehicleStatus {
    @Id
    private String vehicleCode;
    private String licensePlateNumber;
    private String name;
    private String category;
    private String fuelType;
    private String status;
    private String remark;
    private String memberCode;
}
