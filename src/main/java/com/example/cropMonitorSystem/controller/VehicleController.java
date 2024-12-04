package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dto.VehicleStatus;
import com.example.cropMonitorSystem.dto.impl.VehicleDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.VehicleNotFoundException;
import com.example.cropMonitorSystem.service.VehicleService;
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
@RequestMapping("api/v1/vehicles")
@CrossOrigin
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveVehicle(@RequestBody VehicleDTO vehicleDTO){
        try{
            vehicleService.saveVehicle(vehicleDTO);
            logger.info("Successfully saved vehicle with ID: {}", vehicleDTO.getVehicleCode());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Data persist error while saving vehicle: {}", vehicleDTO, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while saving vehicle: {}", vehicleDTO, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @GetMapping(value = "/{vehicleId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public VehicleStatus getSelectedVehicle(@PathVariable("vehicleId") String vehicleId){
        if (!Regex.idValidator(vehicleId).matches()){
            logger.warn("Invalid vehicle ID format: {}", vehicleId);
            return new SelectedErrorStatus(1,"Vehicle Code Not Valid");
        }
        return vehicleService.getSelectedVehicle(vehicleId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE','SCIENTIST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehicleDTO> getAllVehicle(){
        try{
            List<VehicleDTO> vehicleDTOS = vehicleService.getAllVehicle();
            logger.info("Successfully retrieved all vehicle. Total count: {}", vehicleDTOS.size());
            return vehicleDTOS;
        }catch (Exception e) {
            logger.error("Error occurred while fetching all vehicle", e);
            return null;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @DeleteMapping(value = "/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable ("vehicleId") String vehicleId){
        try{
            if (!Regex.idValidator(vehicleId).matches()){
                logger.warn("Invalid vehicleId ID format: {}", vehicleId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            vehicleService.deleteVehicle(vehicleId);
            logger.info("Successfully deleted vehicle with ID: {}", vehicleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (VehicleNotFoundException e){
            logger.error("vehicle with ID {} not found for deletion", vehicleId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting vehicle with ID {}", vehicleId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PutMapping(value = "/{vehicleId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateVehicle(@PathVariable ("vehicleId") String vehicleId ,@RequestBody VehicleDTO vehicleDTO){
        try{
            vehicleService.updateVehicle(vehicleId,vehicleDTO);
            logger.info("Successfully updated vehicle with ID: {}", vehicleId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Error occurred while updating vehicle with ID {}: Data persist error", vehicleId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while updating vehicle with ID {}: Unexpected error", vehicleId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
