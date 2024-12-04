package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dto.EquipmentStatus;
import com.example.cropMonitorSystem.dto.impl.EquipmentDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.EquipmentNotFoundException;
import com.example.cropMonitorSystem.service.EquipmentService;
import com.example.cropMonitorSystem.util.Regex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/equipment")
@CrossOrigin
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;
    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveEquipment(@RequestBody EquipmentDTO equipmentDTO){
        try{
            equipmentService.saveEquipment(equipmentDTO);
            logger.info("Successfully saved equipment with ID: {}", equipmentDTO.getEquipmentCode());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Data persist error while saving equipment: {}", equipmentDTO, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while saving equipment: {}", equipmentDTO, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @GetMapping(value = "/{equipmentId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public EquipmentStatus getSelectedEquipment(@PathVariable("equipmentId") String equipmentId){
        if (!Regex.idValidator(equipmentId).matches()){
            logger.warn("Invalid equipment ID format: {}", equipmentId);
            return new SelectedErrorStatus(1,"Equipment Code Not Valid");
        }
        logger.info("Successfully retrieved equipment with ID: {}", equipmentId);
        return equipmentService.getSelectedEquipment(equipmentId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE','SCIENTIST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipmentDTO> getAllEquipment(){
        try {
            List<EquipmentDTO> equipmentList = equipmentService.getAllEquipment();
            logger.info("Successfully retrieved all equipment. Total count: {}", equipmentList.size());
            return equipmentList;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all equipment", e);
            return null;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @DeleteMapping(value = "/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable ("equipmentId") String equipmentId){
        try{
            if (!Regex.idValidator(equipmentId).matches()){
                logger.warn("Invalid equipment ID format: {}", equipmentId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            equipmentService.deleteEquipment(equipmentId);
            logger.info("Successfully deleted equipment with ID: {}", equipmentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (EquipmentNotFoundException e){
            logger.error("Equipment with ID {} not found for deletion", equipmentId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting equipment with ID {}", equipmentId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PutMapping(value = "/{equipmentId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateEquipment(@PathVariable ("equipmentId") String equipmentId ,@RequestBody EquipmentDTO equipmentDTO){
        try{
            equipmentService.updateEquipment(equipmentId,equipmentDTO);
            logger.info("Successfully updated equipment with ID: {}", equipmentId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Error occurred while updating equipment with ID {}: Data persist error", equipmentId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while updating equipment with ID {}: Unexpected error", equipmentId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
