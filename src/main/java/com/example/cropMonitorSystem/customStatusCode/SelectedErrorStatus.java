package com.example.cropMonitorSystem.customStatusCode;

import com.example.cropMonitorSystem.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SelectedErrorStatus implements CropStatus, EquipmentStatus, FieldStatus, LogStatus, StaffStatus,UserStatus,VehicleStatus, SuperDTO, Serializable {
    private Integer status;
    private String statusMessage;
}
