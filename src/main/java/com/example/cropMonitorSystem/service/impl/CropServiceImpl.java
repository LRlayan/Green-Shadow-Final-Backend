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
            }
        }
        if (cropDTO.getLogCodeList()!=null) {
            for (String logCode : cropDTO.getLogCodeList()) {
                if (logDAO.existsById(logCode)) {
                    logEntities.add(logDAO.getReferenceById(logCode));
                }
            }
        }
        cropEntity.setFieldList(fieldEntities);
        cropEntity.setLogList(logEntities);
        CropEntity save = cropDAO.save(cropEntity);
        for (FieldEntity fieldEntity : fieldEntities){
            fieldEntity.getCropList().add(save);
        }
        for (LogEntity logEntity:logEntities){
            logEntity.getCropList().add(save);
        }
        if (cropEntity == null){
            throw new DataPersistException("Crop is not saved.");
        }
    }

    @Override
    public List<CropDTO> getAllCrop() {
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
            }
            for (FieldEntity fieldEntity:fieldEntities){
                List<CropEntity> crop = fieldEntity.getCropList();
                crop.remove(cropEntity);
            }
            cropEntity.getLogList().clear();
            cropEntity.getFieldList().clear();
        }
        if (!selectedCrop.isPresent()){
            throw new CropNotFoundException("Crop with id " + id + "not found");
        }else {
            cropDAO.deleteById(id);
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
        }
    }

    @Override
    public CropStatus getSelectedCrop(String cropId) {
        return null;
    }
}
