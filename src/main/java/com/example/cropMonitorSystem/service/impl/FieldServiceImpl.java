package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dao.*;
import com.example.cropMonitorSystem.dto.FieldStatus;
import com.example.cropMonitorSystem.dto.impl.*;
import com.example.cropMonitorSystem.entity.impl.*;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.FieldNotFoundException;
import com.example.cropMonitorSystem.service.FieldService;
import com.example.cropMonitorSystem.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FieldServiceImpl implements FieldService {
    @Autowired
    private FieldDAO fieldDAO;
    @Autowired
    private StaffDAO staffDAO;
    @Autowired
    private LogDAO logDAO;
    @Autowired
    private CropDAO cropDAO;
    @Autowired
    private EquipmentDAO equipmentDAO;
    @Autowired
    private Mapping mapping;
    @Override
    public void saveField(FieldDTO fieldDTO) {
        int number = 0;
        FieldEntity field = fieldDAO.findLastRowNative();
        if (field != null){
            String[] parts = field.getFieldCode().split("-");
            number = Integer.parseInt(parts[1]);
        }
        fieldDTO.setFieldCode("FIELD-" + ++number);
        List<StaffEntity> staffEntities = new ArrayList<>();
        List<CropEntity> cropEntities = new ArrayList<>();
        if(fieldDTO.getMemberCodeList()!=null || fieldDTO.getCropCodeList()!=null){
            for (String id : fieldDTO.getMemberCodeList()){
                if (staffDAO.existsById(id)){
                    staffEntities.add(staffDAO.getReferenceById(id));
                }
            }
            for (String id : fieldDTO.getCropCodeList()){
                if (cropDAO.existsById(id)){
                    cropEntities.add(cropDAO.getReferenceById(id));
                }
            }
        }
        FieldEntity fieldEntity = mapping.toFieldEntity(fieldDTO);
        fieldEntity.setLocation(fieldDTO.getLocation());
        fieldEntity.setStaffList(staffEntities);
        fieldEntity.setCropList(cropEntities);
        FieldEntity saveField = fieldDAO.save(fieldEntity);
        if (saveField == null){
            throw new DataPersistException("Field is not saved.");
        }
    }

    @Override
    public List<FieldDTO> getAllField() throws IOException, ClassNotFoundException {
        List<FieldDTO> fieldDTOS = new ArrayList<>();
        for (FieldEntity fieldEntity : fieldDAO.findAll()){
            List<String> staffCode = new ArrayList<>();
            List<String> logCode = new ArrayList<>();
            for (StaffEntity staffEntity : fieldEntity.getStaffList()){
                staffCode.add(staffEntity.getMemberCode());
            }
            for (LogEntity logEntity :fieldEntity.getLogList()){
                logCode.add(logEntity.getLogCode());
            }
            FieldDTO fieldDTO = mapping.toGetAllFieldDTO(fieldEntity);
            fieldDTO.setMemberCodeList(staffCode);
            fieldDTO.setLogCodeList(logCode);
            fieldDTOS.add(fieldDTO);
        }
        return fieldDTOS;
    }

    @Override
    public void deleteField(String id) throws FieldNotFoundException {
        Optional<FieldEntity> selectedField = fieldDAO.findById(id);
        if (fieldDAO.existsById(id)){
            FieldEntity fieldEntity = fieldDAO.getReferenceById(id);
            List<EquipmentEntity> equipmentEntities = fieldEntity.getEquipmentsList();
            for (EquipmentEntity equipmentEntity:equipmentEntities){
                List<FieldEntity> fields = equipmentEntity.getFieldList();
                fields.remove(fieldEntity);
            }
            fieldEntity.getEquipmentsList().clear();
        }
        if (!selectedField.isPresent()){
            throw new FieldNotFoundException("Field with id " + id + "not found");
        }else {
            fieldDAO.deleteById(id);
        }
    }

    @Override
    public void updateField(String id, FieldDTO fieldDTO) {
        Optional<FieldEntity> tmpField = fieldDAO.findById(id);
        if (tmpField.isPresent()) {
            tmpField.get().setName(fieldDTO.getName());
            tmpField.get().setLocation(fieldDTO.getLocation());
            tmpField.get().setExtentSize(fieldDTO.getExtentSize());
            tmpField.get().setFieldImage1(fieldDTO.getFieldImage1());
            tmpField.get().setFieldImage2(fieldDTO.getFieldImage2());
            List<CropEntity> cropEntities = new ArrayList<>();
            List<StaffEntity> staffEntities = new ArrayList<>();
            for (String cropCode:fieldDTO.getCropCodeList()){
                cropEntities.add(cropDAO.getReferenceById(cropCode));
            }
            for (String memberCode:fieldDTO.getMemberCodeList()){
                staffEntities.add(staffDAO.getReferenceById(memberCode));
            }
            tmpField.get().setCropList(cropEntities);
            tmpField.get().setStaffList(staffEntities);
        }
    }

    @Override
    public FieldStatus getSelectedField(String fieldId) {
        if (fieldDAO.existsById(fieldId)){
            return mapping.toFieldDTO(fieldDAO.getReferenceById(fieldId));
        }else {
            return new SelectedErrorStatus(2,"Field with Code "+fieldId+" not found");
        }
    }

    public String convertPointToLocation(Point point) {
        if (point != null) {
            return point.getX() + "," + point.getY(); // Format as "longitude,latitude"
        }
        return "";
    }

    private Point location(String location){
        String[] locationParts = location.split(",");
        double longitude = Double.parseDouble(locationParts[0].trim());
        double latitude = Double.parseDouble(locationParts[1].trim());
        return new Point(longitude, latitude);
    }
}