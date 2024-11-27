package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.FieldStatus;
import com.example.cropMonitorSystem.dto.impl.FieldDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.FieldNotFoundException;
import com.example.cropMonitorSystem.service.FieldService;
import com.example.cropMonitorSystem.util.AppUtil;
import com.example.cropMonitorSystem.util.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{fieldId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public FieldStatus getSelectedField(@PathVariable("fieldId") String fieldId){
        return fieldService.getSelectedField(fieldId);
    }

    @GetMapping
    public List<FieldDTO> getAllField(){
        try {
            return fieldService.getAllField();
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @DeleteMapping(value = "/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable ("fieldId") String fieldId){
        try {
            if (!Regex.idValidator(fieldId).matches()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            fieldService.deleteField(fieldId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (FieldNotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
        fieldService.updateField(fieldId,fieldDTO);
    }
}
