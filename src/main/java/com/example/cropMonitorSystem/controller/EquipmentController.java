package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dto.EquipmentStatus;
import com.example.cropMonitorSystem.dto.impl.EquipmentDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.EquipmentNotFoundException;
import com.example.cropMonitorSystem.service.EquipmentService;
import com.example.cropMonitorSystem.util.Regex;
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

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveEquipment(@RequestBody EquipmentDTO equipmentDTO){
        try{
            equipmentService.saveEquipment(equipmentDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @GetMapping(value = "/{equipmentId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public EquipmentStatus getSelectedEquipment(@PathVariable("equipmentId") String equipmentId){
        if (!Regex.idValidator(equipmentId).matches()){
            return new SelectedErrorStatus(1,"Equipment Code Not Valid");
        }
        return equipmentService.getSelectedEquipment(equipmentId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipmentDTO> getAllEquipment(){
        return equipmentService.getAllEquipment();
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @DeleteMapping(value = "/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable ("equipmentId") String equipmentId){
        try{
            if (!Regex.idValidator(equipmentId).matches()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            equipmentService.deleteEquipment(equipmentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (EquipmentNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @PutMapping(value = "/{equipmentId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateEquipment(@PathVariable ("equipmentId") String equipmentId ,@RequestBody EquipmentDTO equipmentDTO){
        try{
            equipmentService.updateEquipment(equipmentId,equipmentDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
