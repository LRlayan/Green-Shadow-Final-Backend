package com.example.cropMonitorSystem.util;

import com.example.cropMonitorSystem.dto.impl.*;
import com.example.cropMonitorSystem.entity.impl.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapping {
    @Autowired
    ModelMapper modelMapper;

    public UserEntity toUserEntity(UserDTO userDTO){
        return modelMapper.map(userDTO,UserEntity.class);
    }

    public UserDTO toUserDTO(UserEntity userentity){
        return modelMapper.map(userentity,UserDTO.class);
    }

    public List<UserDTO> userList(List<UserEntity> userList){
        return modelMapper.map(userList,new TypeToken<List<UserDTO>>(){}.getType());
    }

    public CropEntity toCropEntity(CropDTO cropDTO){
        return modelMapper.map(cropDTO,CropEntity.class);
    }

    public CropDTO toCropDTO(CropEntity cropEntity){
        return modelMapper.map(cropEntity,CropDTO.class);
    }

    public List<CropDTO> cropList(List<CropEntity> cropList){
        return modelMapper.map(cropList,new TypeToken<List<CropDTO>>(){}.getType());
    }

    public EquipmentEntity toEquipmentEntity(EquipmentDTO equipmentDTO){
        return modelMapper.map(equipmentDTO,EquipmentEntity.class);
    }

    public EquipmentDTO toEquipmentDTO(EquipmentEntity equipmentEntity){
        return modelMapper.map(equipmentEntity,EquipmentDTO.class);
    }

    public List<EquipmentDTO> equipmentList(List<EquipmentEntity> equipmentEntities){
        return modelMapper.map(equipmentEntities,new TypeToken<List<EquipmentDTO>>(){}.getType());
    }

    public FieldEntity toFieldEntity(FieldDTO fieldDTO){
        return modelMapper.map(fieldDTO,FieldEntity.class);
    }

    public FieldDTO toFieldDTO(FieldEntity fieldEntity){
        return modelMapper.map(fieldEntity,FieldDTO.class);
    }

    public FieldDTO toGetAllFieldDTO(FieldEntity fieldEntity){
        FieldDTO dto = new FieldDTO();
        dto.setFieldCode(fieldEntity.getFieldCode());
        dto.setName(fieldEntity.getName());
        dto.setLocation(fieldEntity.getLocation());
        dto.setExtentSize(fieldEntity.getExtentSize());
        dto.setFieldImage1(fieldEntity.getFieldImage1());
        dto.setFieldImage2(fieldEntity.getFieldImage2());
        dto.setMemberCodeList(fieldEntity.getStaffList().stream().map(StaffEntity::getMemberCode).toList());
        dto.setCropCodeList(fieldEntity.getCropList().stream().map(CropEntity::getCropCode).toList());
        return dto;
    }

    public List<FieldDTO> fieldList(List<FieldEntity> fieldEntities){
        return modelMapper.map(fieldEntities,new TypeToken<List<FieldDTO>>(){}.getType());
    }

    public List<FieldEntity> fieldListEntity(List<FieldDTO> fieldDTOS){
        return modelMapper.map(fieldDTOS,new TypeToken<List<FieldEntity>>(){}.getType());
    }

    public LogEntity toLogEntity(LogDTO logDTO){
        return modelMapper.map(logDTO,LogEntity.class);
    }

    public LogDTO toLogDTO(LogEntity logEntity){
        return modelMapper.map(logEntity,LogDTO.class);
    }

    public List<LogDTO> logList(List<LogDTO> logDTO){
        return modelMapper.map(logDTO,new TypeToken<List<LogDTO>>(){}.getType());
    }

    public StaffEntity toStaffEntity(StaffDTO staffDTO){
        return modelMapper.map(staffDTO,StaffEntity.class);
    }

    public StaffDTO toStaffDTO(StaffEntity staffEntity){
        return modelMapper.map(staffEntity,StaffDTO.class);
    }

    public List<StaffDTO> staffList(List<StaffEntity> staffEntities){
        return modelMapper.map(staffEntities,new TypeToken<List<StaffDTO>>(){}.getType());
    }

    public VehicleEntity toVehicleEntity(VehicleDTO vehicleDTO){
        return modelMapper.map(vehicleDTO,VehicleEntity.class);
    }

    public VehicleDTO toVehicleDTO(VehicleEntity vehicleEntity){
        return modelMapper.map(vehicleEntity,VehicleDTO.class);
    }

    public List<VehicleDTO> vehicleList(List<VehicleEntity> vehicleEntity){
        return modelMapper.map(vehicleEntity,new TypeToken<List<VehicleDTO>>(){}.getType());
    }
}
