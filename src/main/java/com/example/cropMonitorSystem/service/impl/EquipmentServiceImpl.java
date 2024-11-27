package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dao.EquipmentDAO;
import com.example.cropMonitorSystem.dao.FieldDAO;
import com.example.cropMonitorSystem.dao.StaffDAO;
import com.example.cropMonitorSystem.dto.EquipmentStatus;
import com.example.cropMonitorSystem.dto.impl.EquipmentDTO;
import com.example.cropMonitorSystem.entity.impl.EquipmentEntity;
import com.example.cropMonitorSystem.entity.impl.FieldEntity;
import com.example.cropMonitorSystem.entity.impl.StaffEntity;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.EquipmentNotFoundException;
import com.example.cropMonitorSystem.service.EquipmentService;
import com.example.cropMonitorSystem.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentDAO equipmentDAO;
    @Autowired
    private FieldDAO fieldDAO;
    @Autowired
    private StaffDAO staffDAO;
    @Autowired
    private Mapping mapping;

    @Override
    public void saveEquipment(EquipmentDTO equipmentDTO) {
        int number = 0;
        EquipmentEntity equipment = equipmentDAO.findLastRowNative();
        if (equipment != null){
            String[] parts = equipment.getEquipmentCode().split("-");
            number = Integer.parseInt(parts[1]);
        }
        equipmentDTO.setEquipmentCode("EQUIPMENT-" + ++number);
        List<FieldEntity> fieldEntities = new ArrayList<>();
        List<StaffEntity> staffEntities = new ArrayList<>();
        for (String fieldCode : equipmentDTO.getFieldList()){
            fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
        }
        EquipmentEntity equipmentEntity = mapping.toEquipmentEntity(equipmentDTO);
        for (FieldEntity field:fieldEntities){
            field.getEquipmentsList().add(equipmentEntity);
        }
        for (String staffCode : equipmentDTO.getStaffCodeList()){
            staffEntities.add(staffDAO.getReferenceById(staffCode));
        }
        for (StaffEntity staff:staffEntities){
            staff.getEquipmentList().add(equipmentEntity);
        }
        equipmentEntity.setFieldList(fieldEntities);
        equipmentEntity.setStaffCodeList(staffEntities);
        EquipmentEntity equEntity = equipmentDAO.save(equipmentEntity);
        if (equEntity == null){
            throw new DataPersistException("Equipment not saved!");
        }
    }

    @Override
    public List<EquipmentDTO> getAllEquipment() {
        List<EquipmentDTO> equipmentDTOS = new ArrayList<>();
        for (EquipmentEntity equipmentEntity:equipmentDAO.findAll()){
            List<String> fieldCode = new ArrayList<>();
            List<String> staffCode = new ArrayList<>();
            for (FieldEntity field:equipmentEntity.getFieldList()){
                fieldCode.add(field.getFieldCode());
            }
            for (StaffEntity staff:equipmentEntity.getStaffCodeList()){
                staffCode.add(staff.getMemberCode());
            }
            EquipmentDTO equipmentDTO = mapping.toEquipmentDTO(equipmentEntity);
            equipmentDTO.setFieldList(fieldCode);
            equipmentDTO.setStaffCodeList(staffCode);
            equipmentDTOS.add(equipmentDTO);
        }
        return equipmentDTOS;
    }

    @Override
    public void deleteEquipment(String id) {
        Optional<EquipmentEntity> selectedEquipment = equipmentDAO.findById(id);
        if (!selectedEquipment.isPresent()){
            throw new EquipmentNotFoundException("Equipment Id with" + id + "Not found");
        }else {
            equipmentDAO.deleteById(id);
        }
    }

    @Override
    public void updateEquipment(String id, EquipmentDTO equipmentDTO) {
        Optional<EquipmentEntity> tmpEquipment = equipmentDAO.findById(id);
        if (tmpEquipment.isPresent()){
            tmpEquipment.get().setName(equipmentDTO.getName());
            tmpEquipment.get().setType(equipmentDTO.getType());
            tmpEquipment.get().setStatus(equipmentDTO.getStatus());
            tmpEquipment.get().setAvailableCount(equipmentDTO.getAvailableCount());
            List<FieldEntity> fieldEntities = new ArrayList<>();
            List<StaffEntity> staffEntities = new ArrayList<>();
            for (String fieldCode : equipmentDTO.getFieldList()){
                fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
            }
            for (String staffCode : equipmentDTO.getStaffCodeList()){
                staffEntities.add(staffDAO.getReferenceById(staffCode));
            }
            EquipmentEntity equipmentEntity = mapping.toEquipmentEntity(equipmentDTO);
            equipmentEntity.setFieldList(fieldEntities);
            equipmentEntity.setStaffCodeList(staffEntities);
            for (FieldEntity field:fieldEntities){
                field.getEquipmentsList().add(equipmentEntity);
            }
            for (StaffEntity staffs:staffEntities){
                staffs.getEquipmentList().add(equipmentEntity);
            }
            tmpEquipment.get().setFieldList(equipmentEntity.getFieldList());
            tmpEquipment.get().setStaffCodeList(equipmentEntity.getStaffCodeList());
        }
    }

    @Override
    public EquipmentStatus getSelectedEquipment(String equipmentId) {
        if(equipmentDAO.existsById(equipmentId)){
            var selectedEquipment = equipmentDAO.getReferenceById(equipmentId);
            return mapping.toEquipmentDTO(selectedEquipment);
        }else {
            return new SelectedErrorStatus(2,"Selected Equipment not found");
        }
    }
}
