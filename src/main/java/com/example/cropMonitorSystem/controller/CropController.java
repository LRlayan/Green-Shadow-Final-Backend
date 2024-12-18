package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.service.CropService;
import com.example.cropMonitorSystem.dto.CropStatus;
import com.example.cropMonitorSystem.dto.impl.CropDTO;
import com.example.cropMonitorSystem.exception.CropNotFoundException;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.util.AppUtil;
import com.example.cropMonitorSystem.util.Regex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/crops")
@CrossOrigin
public class CropController {
    @Autowired
    private CropService cropService;
    private static final Logger logger = LoggerFactory.getLogger(CropController.class);

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveCrop(
            @RequestPart("cropName") String cropName,
            @RequestPart("scientificName") String scientificName,
            @RequestPart("category") String category,
            @RequestPart("season") String season,
            @RequestPart("cropImage") MultipartFile cropImage,
            @RequestPart("fieldList") List<String> fieldList
    ) {
        try{
            var cropDTO = new CropDTO();
            cropDTO.setCropName(cropName);
            cropDTO.setScientificName(scientificName);
            cropDTO.setCategory(category);
            cropDTO.setSeason(season);
            cropDTO.setCropImage(AppUtil.imageBase64(cropImage.getBytes()));
            cropDTO.setFieldCodeList(fieldList);
            cropService.saveCrop(cropDTO);
            logger.info("Crop saved successfully with name: {}", cropName);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Error persisting crop data for cropName: {}", cropName, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Unexpected error occurred while saving crop data for cropName: {}", cropName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @GetMapping(value = "/{cropId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public CropStatus getSelectedCrop(@PathVariable ("cropId") String cropId){
        return null;
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST','ADMINISTRATOR')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CropDTO> getAllCrop(){
        try {
            List<CropDTO> crops = cropService.getAllCrop();
            logger.info("Successfully retrieved {} crops.", crops.size());
            return crops;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving crops.", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @DeleteMapping(value = "/{cropId}")
    public ResponseEntity<Void> deleteCrop(@PathVariable ("cropId") String cropId){
        try {
            if (!Regex.idValidator(cropId).matches()){
                logger.warn("Invalid crop ID format: {}", cropId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            cropService.deleteCrop(cropId);
            logger.info("Successfully deleted crop with ID: {}", cropId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (CropNotFoundException e){
            logger.error("Crop not found with ID: {}", cropId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting crop with ID: {}", cropId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @PutMapping(value = "/{cropId}")
    public void updateCrop(
            @PathVariable("cropId") String cropId,
            @RequestPart("cropName") String cropName,
            @RequestPart("scientificName") String scientificName,
            @RequestPart("category") String category,
            @RequestPart("season") String season,
            @RequestPart("cropImage") MultipartFile cropImage
    ) throws IOException {
        CropDTO cropDTO = new CropDTO();
        cropDTO.setCropCode(cropId);
        cropDTO.setCropName(cropName);
        cropDTO.setScientificName(scientificName);
        cropDTO.setCategory(category);
        cropDTO.setSeason(season);
        cropDTO.setCropImage(AppUtil.imageBase64(cropImage.getBytes()));
        cropService.updateCrop(cropId,cropDTO);
        logger.info("Successfully updated crop with ID: {}", cropId);
    }
}
