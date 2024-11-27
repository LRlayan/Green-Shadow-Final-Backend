package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.dto.LogStatus;
import com.example.cropMonitorSystem.dto.impl.LogDTO;
import com.example.cropMonitorSystem.exception.CropNotFoundException;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.service.LogService;
import com.example.cropMonitorSystem.util.AppUtil;
import com.example.cropMonitorSystem.util.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/logs")
@CrossOrigin
public class LogController {
    @Autowired
    private LogService logService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveLog(
            @RequestPart("date") String date,
            @RequestPart("logDetails") String logDetails,
            @RequestPart("observedImage") MultipartFile observedImage,
            @RequestPart("staffList") List<String> staffList,
            @RequestPart("cropList") List<String> cropList,
            @RequestPart("fieldList") List<String> fieldList
    ) {
        try{
            var logDTO = new LogDTO();
            logDTO.setDate(date);
            logDTO.setLogDetails(logDetails);
            logDTO.setObservedImage(AppUtil.imageBase64(observedImage.getBytes()));
            logDTO.setStaffList(staffList);
            logDTO.setCropList(cropList);
            logDTO.setFieldList(fieldList);
            logService.saveLog(logDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{logId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public LogStatus getSelectedLog(@PathVariable("logId") String logId){
        return null;
    }

    @GetMapping
    public List<LogDTO> getAllLog(){
        return logService.getAllLog();
    }

    @DeleteMapping(value = "/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable ("logId") String logId){
        try {
            if (!Regex.idValidator(logId).matches()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            logService.deleteLog(logId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (CropNotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{logId}")
    public void updateLog(
            @PathVariable("logId") String logId,
            @RequestPart("date") String logDate,
            @RequestPart("logDetails") String logDetails,
            @RequestPart("observedImage") MultipartFile observedImage,
            @RequestPart("staffList") List<String> staffList,
            @RequestPart("cropList") List<String> cropList,
            @RequestPart("fieldList") List<String> fieldList
    ) throws IOException {
        var logDTO = new LogDTO();
        logDTO.setDate(logDate);
        logDTO.setLogDetails(logDetails);
        logDTO.setObservedImage(AppUtil.imageBase64(observedImage.getBytes()));
        logDTO.setStaffList(staffList);
        logDTO.setCropList(cropList);
        logDTO.setFieldList(fieldList);
        logService.updateLog(logId,logDTO);
    }
}
