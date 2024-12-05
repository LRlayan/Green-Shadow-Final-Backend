package com.example.cropMonitorSystem.service.impl;

import com.example.cropMonitorSystem.dao.CropDAO;
import com.example.cropMonitorSystem.dao.FieldDAO;
import com.example.cropMonitorSystem.dao.LogDAO;
import com.example.cropMonitorSystem.dto.CropStatus;
import com.example.cropMonitorSystem.dto.impl.CropDTO;
import com.example.cropMonitorSystem.entity.impl.CropEntity;
import com.example.cropMonitorSystem.entity.impl.FieldEntity;
import com.example.cropMonitorSystem.entity.impl.LogEntity;
import com.example.cropMonitorSystem.exception.CropNotFoundException;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.service.CropService;
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
public class CropServiceImpl implements CropService {
    @Autowired
    private CropDAO cropDAO;
    @Autowired
    private FieldDAO fieldDAO;
    @Autowired
    private LogDAO logDAO;
    @Autowired
    private Mapping mapping;
    private static final Logger logger = LoggerFactory.getLogger(CropServiceImpl.class);

    @Override
    public void saveCrop(CropDTO cropDTO) {
        int number = 0;
        CropEntity crop = cropDAO.findLastRowNative();
        if (crop != null){
            String[] parts = crop.getCropCode().split("-");
            number = Integer.parseInt(parts[1]);
        }
        cropDTO.setCropCode("CROP-" + ++number);
        CropEntity cropEntity = mapping.toCropEntity(cropDTO);
        List<FieldEntity> fieldEntities = new ArrayList<>();
        List<LogEntity> logEntities = new ArrayList<>();
        for (String fieldCode : cropDTO.getFieldCodeList()){
            if (fieldDAO.existsById(fieldCode)){
                fieldEntities.add(fieldDAO.getReferenceById(fieldCode));
                logger.debug("Field with code {} found and associated.", fieldCode);
            }else {
                logger.warn("Field with code {} does not exist.", fieldCode);
            }
        }
        if (cropDTO.getLogCodeList()!=null) {
            for (String logCode : cropDTO.getLogCodeList()) {
                if (logDAO.existsById(logCode)) {
                    logEntities.add(logDAO.getReferenceById(logCode));
                    logger.debug("Log with code {} found and associated.", logCode);
                }else {
                    logger.warn("Log with code {} does not exist.", logCode);
                }
            }
        }
        cropEntity.setFieldList(fieldEntities);
        cropEntity.setLogList(logEntities);
        CropEntity save = cropDAO.save(cropEntity);
        logger.info("Crop saved with code: {}", save.getCropCode());
        for (FieldEntity fieldEntity : fieldEntities){
            fieldEntity.getCropList().add(save);
            logger.debug("Updated field entity {} with crop.", fieldEntity.getFieldCode());
        }
        for (LogEntity logEntity:logEntities){
            logEntity.getCropList().add(save);
            logger.debug("Updated log entity {} with crop.", logEntity.getLogCode());
        }
        if (cropEntity == null){
            logger.error("Failed to save crop. CropEntity is null.");
            throw new DataPersistException("Crop is not saved.");
        }
    }

    @Override
    public List<CropDTO> getAllCrop() {
        logger.info("Fetching and mapping all crops from the database.");
        List<CropDTO> cropDTOS =  new ArrayList<>();
        for (CropEntity cropEntity :  cropDAO.findAll()){
            List<String> fieldCodes = new ArrayList<>();
            List<String> logCodes = new ArrayList<>();
            for (FieldEntity field : cropEntity.getFieldList()){
                fieldCodes.add(field.getFieldCode());
            }
            for (LogEntity logs : cropEntity.getLogList()){
                logCodes.add(logs.getLogCode());
            }
            CropDTO cropDTO = mapping.toCropDTO(cropEntity);
            cropDTO.setFieldCodeList(fieldCodes);
            cropDTO.setLogCodeList(logCodes);
            cropDTOS.add(cropDTO);
        }
        logger.info("Successfully fetched and mapped {} crops.", cropDTOS.size());
        return cropDTOS;
    }

    @Override
    public void deleteCrop(String id) {
        Optional<CropEntity> selectedCrop = cropDAO.findById(id);
        if (cropDAO.existsById(id)){
            CropEntity cropEntity = cropDAO.getReferenceById(id);
            List<LogEntity> logEntities = cropEntity.getLogList();
            List<FieldEntity> fieldEntities = cropEntity.getFieldList();
            for (LogEntity logEntity:logEntities){
                List<CropEntity> crop = logEntity.getCropList();
                crop.remove(cropEntity);
                logger.debug("Removed crop from LogEntity with ID: {}", logEntity.getLogCode());
            }
            for (FieldEntity fieldEntity:fieldEntities){
                List<CropEntity> crop = fieldEntity.getCropList();
                crop.remove(cropEntity);
                logger.debug("Removed crop from FieldEntity with ID: {}", fieldEntity.getFieldCode());
            }
            cropEntity.getLogList().clear();
            cropEntity.getFieldList().clear();
            logger.debug("Cleared relationships for CropEntity with ID: {}", id);
        }
        if (!selectedCrop.isPresent()){
            throw new CropNotFoundException("Crop with id " + id + "not found");
        }else {
            cropDAO.deleteById(id);
            logger.info("Successfully deleted crop with ID: {}", id);
        }
    }

    @Override
    public void updateCrop(String id, CropDTO cropDTO) {
        Optional<CropEntity> tmpCrop = cropDAO.findById(id);
        if (tmpCrop.isPresent()) {
            tmpCrop.get().setCropName(cropDTO.getCropName());
            tmpCrop.get().setScientificName(cropDTO.getScientificName());
            tmpCrop.get().setCategory(cropDTO.getCategory());
            tmpCrop.get().setSeason(cropDTO.getSeason());
            tmpCrop.get().setCropImage(cropDTO.getCropImage());
            logger.info("Successfully updated crop with ID: {}", id);
        }
    }

    @Override
    public CropStatus getSelectedCrop(String cropId) {
        return null;
    }
}
