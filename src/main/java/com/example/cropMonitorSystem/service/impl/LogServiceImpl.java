package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.CropDAO;
import com.example.cropMonitorSystem.dao.FieldDAO;
import com.example.cropMonitorSystem.dao.LogDAO;
import com.example.cropMonitorSystem.dao.StaffDAO;
import com.example.cropMonitorSystem.dto.LogStatus;
import com.example.cropMonitorSystem.dto.impl.LogDTO;
import com.example.cropMonitorSystem.entity.impl.CropEntity;
import com.example.cropMonitorSystem.entity.impl.FieldEntity;
import com.example.cropMonitorSystem.entity.impl.LogEntity;
import com.example.cropMonitorSystem.entity.impl.StaffEntity;
import com.example.cropMonitorSystem.exception.CropNotFoundException;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.service.FieldService;
import com.example.cropMonitorSystem.service.LogService;
import com.example.cropMonitorSystem.util.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LogServiceImpl implements LogService {
    @Autowired
    private LogDAO logDAO;
    @Autowired
    private CropDAO cropDAO;
    @Autowired
    private StaffDAO staffDAO;
    @Autowired
    private FieldDAO fieldDAO;
    @Autowired
    private Mapping mapping;
    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public void saveLog(LogDTO logDTO) {
        int number = 0;
        LogEntity log = logDAO.findLastRowNative();
        if (log != null){
            String[] parts = log.getLogCode().split("-");
            number = Integer.parseInt(parts[1]);
        }
        logDTO.setLogCode("LOG-" + ++number);
        List<StaffEntity> staffEntities = new ArrayList<>();
        List<FieldEntity> fieldEntities = new ArrayList<>();
        List<CropEntity> cropEntities = new ArrayList<>();
        for (String cropCode :logDTO.getCropList()){
            if (cropDAO.existsById(cropCode)){
                cropEntities.add(cropDAO.getReferenceById(cropCode));
                logger.debug("Crop with code {} found and associated.", cropCode);
            }else {
                logger.warn("Crop with code {} does not exist.", cropCode);
            }
        }
        for (String fieldCode:logDTO.getFieldList()){
            if (fieldDAO.existsById(fieldCode)){
                fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
                logger.debug("Field with code {} found and associated.", fieldCode);
            }else {
                logger.warn("Field with code {} does not exist.", fieldCode);
            }
        }
        for (String staffCode:logDTO.getStaffList()){
            if (staffDAO.existsById(staffCode)){
                staffEntities.add(staffDAO.getReferenceById(staffCode));
                logger.debug("Staff with code {} found and associated.", staffCode);
            }else {
                logger.warn("Staff with code {} does not exist.", staffCode);
            }
        }
        LogEntity logEntity = mapping.toLogEntity(logDTO);
        logEntity.setCropList(cropEntities);
        logEntity.setStaffList(staffEntities);
        logEntity.setFieldList(fieldEntities);
        for (FieldEntity fieldEntity:fieldEntities){
            fieldEntity.getLogList().add(logEntity);
        }
        LogEntity saveLog = logDAO.save(logEntity);
        logger.info("Log saved with code: {}", saveLog.getLogCode());
        if (saveLog==null){
            throw new DataPersistException("Field is not saved.");
        }
    }

    @Override
    public List<LogDTO> getAllLog() {
        logger.info("Fetching and mapping all log from the database.");
        List<LogDTO> logDTOS = new ArrayList<>();
        for (LogEntity logEntity:logDAO.findAll()){
            List<String> fieldCode = new ArrayList<>();
            List<String> cropCode = new ArrayList<>();
            List<String> staffCode = new ArrayList<>();
            for (FieldEntity fieldEntity:logEntity.getFieldList()){
                fieldCode.add(fieldEntity.getFieldCode());
            }
            for (CropEntity cropEntity:logEntity.getCropList()){
                cropCode.add(cropEntity.getCropCode());
            }
            for (StaffEntity staffEntity:logEntity.getStaffList()){
                staffCode.add(staffEntity.getMemberCode());
            }
            LogDTO logDTO = mapping.toLogDTO(logEntity);
            logDTO.setFieldList(fieldCode);
            logDTO.setCropList(cropCode);
            logDTO.setStaffList(staffCode);
            logDTOS.add(logDTO);
        }
        logger.info("Successfully fetched and mapped {} log.", logDTOS.size());
        return logDTOS;
    }

    @Override
    public void deleteLog(String id) {
        if (logDAO.existsById(id)){
            LogEntity logEntity = logDAO.getReferenceById(id);
            List<FieldEntity> fieldList = logEntity.getFieldList();
            List<CropEntity> cropList = logEntity.getCropList();
            List<StaffEntity> staffList = logEntity.getStaffList();
            for (FieldEntity fieldEntity : fieldList){
                List<LogEntity> logEntities = fieldEntity.getLogList();
                logEntities.remove(logEntity);
                logger.debug("Removed Field from FieldEntity with ID: {}", fieldEntity.getFieldCode());
            }
            for (CropEntity cropEntity : cropList){
                List<LogEntity> logEntities = cropEntity.getLogList();
                logEntities.remove(logEntity);
                logger.debug("Removed Field from CropEntity with ID: {}", cropEntity.getCropCode());
            }
            for (StaffEntity staffEntity : staffList){
                List<LogEntity> logEntities = staffEntity.getLogList();
                logEntities.remove(logEntity);
                logger.debug("Removed Staff from StaffEntity with ID: {}", staffEntity.getMemberCode());
            }
            logEntity.getFieldList().clear();
            logEntity.getStaffList().clear();
            logEntity.getCropList().clear();
            logger.debug("Cleared relationships for LogEntity with ID: {}", id);
            logDAO.delete(logEntity);
            logger.info("Successfully deleted log with ID: {}", id);
        }else {
            throw new CropNotFoundException("Log with id " + id + "not found");
        }
    }

    @Override
    public void updateLog(String id, LogDTO logDTO) {
        Optional<LogEntity> tmpLog = logDAO.findById(id);
        if (tmpLog.isPresent()){
            tmpLog.get().setDate(logDTO.getDate());
            tmpLog.get().setLogDetails(logDTO.getLogDetails());
            tmpLog.get().setObservedImage(logDTO.getObservedImage());
            List<StaffEntity> staffEntities = new ArrayList<>();
            List<FieldEntity> fieldEntities = new ArrayList<>();
            List<CropEntity> cropEntities = new ArrayList<>();
            for (String staffCode :logDTO.getStaffList()){
                staffEntities.add(staffDAO.getReferenceById(staffCode));
            }
            for (String cropCode :logDTO.getCropList()){
                cropEntities.add(cropDAO.getReferenceById(cropCode));
            }
            for (String fieldCode :logDTO.getFieldList()){
                fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
            }
            tmpLog.get().setStaffList(staffEntities);
            tmpLog.get().setCropList(cropEntities);
            tmpLog.get().setFieldList(fieldEntities);
            logger.info("Successfully updated log with ID: {}", id);
        }
    }

    @Override
    public LogStatus getSelectedLog(String logId) {
        return null;
    }
}
