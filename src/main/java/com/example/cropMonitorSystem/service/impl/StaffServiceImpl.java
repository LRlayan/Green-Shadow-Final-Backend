package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dao.*;
import com.example.cropMonitorSystem.dto.StaffStatus;
import com.example.cropMonitorSystem.dto.impl.StaffDTO;
import com.example.cropMonitorSystem.entity.impl.*;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.StaffNotFoundException;
import com.example.cropMonitorSystem.service.StaffService;
import com.example.cropMonitorSystem.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {
    @Autowired
    private StaffDAO staffDAO;
    @Autowired
    private Mapping mapping;
    @Autowired
    private FieldDAO fieldDAO;
    @Autowired
    private VehicleDAO vehicleDAO;
    @Autowired
    private EquipmentDAO equipmentDAO;

    @Override
    public void saveStaffMember(StaffDTO staffDTO) {
        int number = 0;
        StaffEntity staff = staffDAO.findLastRowNative();
        if (staff != null) {
            String[] parts = staff.getMemberCode().split("-");
            number = Integer.parseInt(parts[1]);
        }
        staffDTO.setMemberCode("MEMBER-" + ++number);

        List<FieldEntity> fieldEntities = new ArrayList<>();
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        List<EquipmentEntity> equipmentEntities = new ArrayList<>();
        for (String fieldCode : staffDTO.getFieldCodeList()){
            fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
        }
        for (String vehicleCode : staffDTO.getVehicleList()){
            vehicleEntities.add(vehicleDAO.getReferenceById(vehicleCode));
        }
        for (String equipmentCode : staffDTO.getEquipmentList()){
            equipmentEntities.add(equipmentDAO.getReferenceById(equipmentCode));
        }
        StaffEntity staffEntity = mapping.toStaffEntity(staffDTO);
        staffEntity.setFieldList(fieldEntities);
        staffEntity.setVehicleList(vehicleEntities);
        staffEntity.setEquipmentList(equipmentEntities);
        staffEntity.setJoinedDate(toConvertLocalDate(staffDTO.getJoinedDate()));
        staffEntity.setDateOfBirth(toConvertLocalDate(staffDTO.getDateOfBirth()));
        StaffEntity savedStaff = staffDAO.save(staffEntity);
        for (FieldEntity field : fieldEntities){
            field.getStaffList().add(savedStaff);
        }
        for (EquipmentEntity equipment : equipmentEntities){
            equipment.getStaffCodeList().add(savedStaff);
        }
        if (savedStaff == null) {
            throw new DataPersistException("Staff member not saved");
        }
    }

    @Override
    public List<StaffDTO> getAllStaffMember() {
        List<StaffDTO> staffDTOS = new ArrayList<>();

        for (StaffEntity staff : staffDAO.findAll()){
            List<String> list = new ArrayList<>();
            List<String> vehicleCodeList = new ArrayList<>();
            List<String> logCodeList = new ArrayList<>();
            List<String> equipmentCodeList = new ArrayList<>();
            for (FieldEntity field : staff.getFieldList()){
                list.add(field.getFieldCode());
            }
            for (VehicleEntity vehicleCodes:staff.getVehicleList()){
                vehicleCodeList.add(vehicleCodes.getVehicleCode());
            }
            for (LogEntity logCode:staff.getLogList()){
                logCodeList.add(logCode.getLogCode());
            }
            for (EquipmentEntity equipmentCode:staff.getEquipmentList()){
                equipmentCodeList.add(equipmentCode.getEquipmentCode());
            }
            StaffDTO staffDTO = mapping.toStaffDTO(staff);
            staffDTO.setFieldCodeList(list);
            staffDTO.setVehicleList(vehicleCodeList);
            staffDTO.setEquipmentList(equipmentCodeList);
            staffDTO.setLogList(logCodeList);
            staffDTOS.add(staffDTO);
        }
        return staffDTOS;
    }

    @Override
    public void deleteStaffMember(String staffId) throws StaffNotFoundException {
        if (staffDAO.existsById(staffId)){
            StaffEntity staffEntity = staffDAO.getReferenceById(staffId);
            List<FieldEntity> fieldList = staffEntity.getFieldList();
            List<VehicleEntity> vehicleList = staffEntity.getVehicleList();
            List<LogEntity> logList = staffEntity.getLogList();
            for (FieldEntity field : fieldList){
                List<StaffEntity> staff = field.getStaffList();
                staff.remove(staffEntity);
            }
            for (VehicleEntity vehicle : vehicleList){
                vehicle.setStaff(null);
            }
            for (LogEntity logs : logList){
                List<StaffEntity> staff = logs.getStaffList();
                staff.remove(staffEntity);
            }
            staffEntity.getFieldList().clear();
            staffEntity.getVehicleList().clear();
            staffEntity.getLogList().clear();
            staffDAO.delete(staffEntity);
        }else {
            throw new StaffNotFoundException("Member Id with" + staffId + "Not found");
        }
    }

    @Override
    public void updateStaffMember(String id, StaffDTO staffDTO) {
        Optional<StaffEntity> tmpMember = staffDAO.findById(id);
        if (tmpMember.isPresent()){
            tmpMember.get().setFirstName(staffDTO.getFirstName());
            tmpMember.get().setLastName(staffDTO.getLastName());
            tmpMember.get().setJoinedDate(LocalDate.parse(staffDTO.getJoinedDate()));
            tmpMember.get().setDateOfBirth(LocalDate.parse(staffDTO.getDateOfBirth()));
            tmpMember.get().setGender(staffDTO.getGender());
            tmpMember.get().setDesignation(staffDTO.getDesignation());
            tmpMember.get().setAddressLine1(staffDTO.getAddressLine1());
            tmpMember.get().setAddressLine2(staffDTO.getAddressLine2());
            tmpMember.get().setAddressLine3(staffDTO.getAddressLine3());
            tmpMember.get().setAddressLine4(staffDTO.getAddressLine4());
            tmpMember.get().setAddressLine5(staffDTO.getAddressLine5());
            tmpMember.get().setContactNo(staffDTO.getContactNo());
            tmpMember.get().setEmail(staffDTO.getEmail());
            tmpMember.get().setRole(staffDTO.getRole());
        }
    }

    @Override
    public StaffStatus getSelectedStaffMember(String staffId) {
        if(staffDAO.existsById(staffId)){
            var selectedMember = staffDAO.getReferenceById(staffId);
            return mapping.toStaffDTO(selectedMember);
        }else {
            return new SelectedErrorStatus(2,"Selected Member not found");
        }
    }

    protected LocalDate toConvertLocalDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        return LocalDate.parse(date,formatter);
    }
}
