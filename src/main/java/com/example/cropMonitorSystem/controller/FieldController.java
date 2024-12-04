package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.FieldStatus;
import com.example.cropMonitorSystem.dto.impl.FieldDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.FieldNotFoundException;
import com.example.cropMonitorSystem.service.FieldService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/fields")
@CrossOrigin
public class FieldController {
    @Autowired
    private FieldService fieldService;
    private static final Logger logger = LoggerFactory.getLogger(FieldController.class);

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveField(
            @RequestPart("name") String fieldName,
            @RequestPart("location") String location,
            @RequestPart("extentSize") String extentSize,
            @RequestPart("fieldImage1") MultipartFile fieldImage1,
            @RequestPart("fieldImage2") MultipartFile fieldImage2,
            @RequestPart("staffList") List<String> staffList,
            @RequestPart("cropList") List<String> cropList
    ) {
        try {
            var fieldDTO = new FieldDTO();
            fieldDTO.setName(fieldName);
            fieldDTO.setLocation(location);
            fieldDTO.setExtentSize(Double.parseDouble(extentSize));
            fieldDTO.setFieldImage1(AppUtil.imageBase64(fieldImage1.getBytes()));
            fieldDTO.setFieldImage2(AppUtil.imageBase64(fieldImage2.getBytes()));
            fieldDTO.setMemberCodeList(staffList);
            fieldDTO.setCropCodeList(cropList);
            fieldService.saveField(fieldDTO);
            logger.info("Successfully saved field with name: {}", fieldName);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Error occurred while saving field with name {}: Data persist error", fieldName);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while saving field with name {}: Unexpected error", fieldName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @GetMapping(value = "/{fieldId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public FieldStatus getSelectedField(@PathVariable("fieldId") String fieldId){
        try {
            FieldStatus fieldStatus = fieldService.getSelectedField(fieldId);
            logger.info("Successfully retrieved field details for field ID: {}", fieldId);
            return fieldStatus;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving field with ID: {}", fieldId, e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST','ADMINISTRATOR')")
    @GetMapping("/{getAllFields}")
    public List<FieldDTO> getAllField() throws IOException, ClassNotFoundException {
        try {
            List<FieldDTO> fields = fieldService.getAllField();
            logger.info("Successfully retrieved all field details.", fields.size());
            return fields;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error occurred while retrieving all field details with parameter: {}", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @DeleteMapping(value = "/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable ("fieldId") String fieldId){
        try {
            if (!Regex.idValidator(fieldId).matches()){
                logger.warn("Invalid field ID format: {}", fieldId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            fieldService.deleteField(fieldId);
            logger.info("Successfully deleted field with ID: {}", fieldId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (FieldNotFoundException e){
            logger.error("Field with ID {} not found: {}", fieldId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting field with ID {}: {}", fieldId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','SCIENTIST')")
    @PutMapping(value = "/{fieldId}")
    public void updateField(
            @PathVariable("fieldId") String fieldId ,
            @RequestPart("name") String name,
            @RequestPart("location") String location,
            @RequestPart("extentSize") String extentSize,
            @RequestPart("fieldImage1") MultipartFile fieldImage1,
            @RequestPart("fieldImage2") MultipartFile fieldImage2,
            @RequestPart("memberCodeList") List<String> staffList,
            @RequestPart("cropCodeList") List<String> cropList
    ) throws IOException {
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setFieldCode(fieldId);
        fieldDTO.setName(name);
        fieldDTO.setLocation(location);
        fieldDTO.setExtentSize(Double.parseDouble(extentSize));
        fieldDTO.setFieldImage1(AppUtil.imageBase64(fieldImage1.getBytes()));
        fieldDTO.setFieldImage2(AppUtil.imageBase64(fieldImage2.getBytes()));
        fieldDTO.setMemberCodeList(staffList);
        fieldDTO.setCropCodeList(cropList);
        logger.info("Successfully updated field with ID: {}", fieldId);
        fieldService.updateField(fieldId,fieldDTO);
    }
}
