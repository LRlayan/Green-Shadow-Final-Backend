package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dao.*;
import com.example.cropMonitorSystem.dto.StaffStatus;
import com.example.cropMonitorSystem.dto.impl.StaffDTO;
import com.example.cropMonitorSystem.entity.impl.*;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.StaffNotFoundException;
import com.example.cropMonitorSystem.service.EquipmentService;
import com.example.cropMonitorSystem.service.StaffService;
import com.example.cropMonitorSystem.util.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

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
            logger.debug("Field with code {} found and associated.", fieldCode);
        }
        for (String vehicleCode : staffDTO.getVehicleList()){
            vehicleEntities.add(vehicleDAO.getReferenceById(vehicleCode));
            logger.debug("Vehicle with code {} found and associated.", vehicleCode);
        }
        for (String equipmentCode : staffDTO.getEquipmentList()){
            equipmentEntities.add(equipmentDAO.getReferenceById(equipmentCode));
            logger.debug("Equipment with code {} found and associated.", equipmentCode);
        }
        StaffEntity staffEntity = mapping.toStaffEntity(staffDTO);
        staffEntity.setFieldList(fieldEntities);
        staffEntity.setVehicleList(vehicleEntities);
        staffEntity.setEquipmentList(equipmentEntities);
        staffEntity.setJoinedDate(toConvertLocalDate(staffDTO.getJoinedDate()));
        staffEntity.setDateOfBirth(toConvertLocalDate(staffDTO.getDateOfBirth()));
        StaffEntity savedStaff = staffDAO.save(staffEntity);
        logger.info("Staff saved with code: {}", staffEntity.getMemberCode());
        for (FieldEntity field : fieldEntities){
            field.getStaffList().add(savedStaff);
        }
        for (EquipmentEntity equipment : equipmentEntities){
            equipment.getStaffCodeList().add(savedStaff);
        }
        for (VehicleEntity vehicle : vehicleEntities){
            vehicle.setStaff(savedStaff);
        }
        if (savedStaff == null) {
            logger.error("Failed to staff. StaffEntity is null.");
            throw new DataPersistException("Staff member not saved");
        }
    }

    @Override
    public List<StaffDTO> getAllStaffMember() {
        logger.info("Fetching and mapping all staff from the database.");
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
        logger.info("Successfully fetched and mapped {} staff.", staffDTOS.size());
        return staffDTOS;
    }

    @Override
    public void deleteStaffMember(String staffId) throws StaffNotFoundException {
        if (staffDAO.existsById(staffId)){
            StaffEntity staffEntity = staffDAO.getReferenceById(staffId);
            List<FieldEntity> fieldList = staffEntity.getFieldList();
            List<VehicleEntity> vehicleList = staffEntity.getVehicleList();
            List<LogEntity> logList = staffEntity.getLogList();
            List<EquipmentEntity> equipmentEntity = staffEntity.getEquipmentList();
            for (FieldEntity field : fieldList){
                List<StaffEntity> staff = field.getStaffList();
                staff.remove(staffEntity);
                logger.debug("Removed Field from FieldEntity with ID: {}", field.getFieldCode());
            }
            for (VehicleEntity vehicle : vehicleList){
                vehicle.setStaff(null);
            }
            for (LogEntity logs : logList){
                List<StaffEntity> staff = logs.getStaffList();
                staff.remove(staffEntity);
                logger.debug("Removed Log from StaffEntity with ID: {}", logs.getLogCode());
            }
            for (EquipmentEntity equipment : equipmentEntity){
                List<StaffEntity> staff = equipment.getStaffCodeList();
                staff.remove(staffEntity);
                logger.debug("Removed Equipment from StaffEntity with ID: {}", equipment.getEquipmentCode());
            }
            staffEntity.getFieldList().clear();
            staffEntity.getVehicleList().clear();
            staffEntity.getLogList().clear();
            staffEntity.getEquipmentList().clear();
            logger.debug("Cleared relationships for StaffEntity with ID: {}", staffId);
            staffDAO.delete(staffEntity);
            logger.info("Successfully deleted staff with ID: {}", staffId);
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
            logger.info("Successfully updated staff with ID: {}", id);
        }
    }

    @Override
    public StaffStatus getSelectedStaffMember(String staffId) {
        if(staffDAO.existsById(staffId)){
            var selectedMember = staffDAO.getReferenceById(staffId);
            logger.info("Staff with ID {} successfully fetched.", staffId);
            return mapping.toStaffDTO(selectedMember);
        }else {
            logger.warn("Staff with ID {} not found.", staffId);
            return new SelectedErrorStatus(2,"Selected Member not found");
        }
    }

    protected LocalDate toConvertLocalDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        return LocalDate.parse(date,formatter);
    }
}
