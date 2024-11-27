package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.VehicleStatus;
import com.example.cropMonitorSystem.dto.impl.VehicleDTO;
import com.example.cropMonitorSystem.exception.VehicleNotFoundException;

import java.util.List;

public interface VehicleService {
    void saveVehicle(VehicleDTO vehicleDTO);
    List<VehicleDTO> getAllVehicle();
    void deleteVehicle(String id) throws VehicleNotFoundException;
    void updateVehicle(String id, VehicleDTO vehicleDTO);
    VehicleStatus getSelectedVehicle(String id);
}
