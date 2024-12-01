package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dto.VehicleStatus;
import com.example.cropMonitorSystem.dto.impl.VehicleDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.VehicleNotFoundException;
import com.example.cropMonitorSystem.service.VehicleService;
import com.example.cropMonitorSystem.util.Regex;
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

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveVehicle(@RequestBody VehicleDTO vehicleDTO){
        try{
            vehicleService.saveVehicle(vehicleDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @GetMapping(value = "/{vehicleId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public VehicleStatus getSelectedVehicle(@PathVariable("vehicleId") String vehicleId){
        if (!Regex.idValidator(vehicleId).matches()){
            return new SelectedErrorStatus(1,"Vehicle Code Not Valid");
        }
        return vehicleService.getSelectedVehicle(vehicleId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehicleDTO> getAllVehicle(){
        return vehicleService.getAllVehicle();
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @DeleteMapping(value = "/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable ("vehicleId") String vehicleId){
        try{
            if (!Regex.idValidator(vehicleId).matches()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            vehicleService.deleteVehicle(vehicleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (VehicleNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVE')")
    @PutMapping(value = "/{vehicleId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateVehicle(@PathVariable ("vehicleId") String vehicleId ,@RequestBody VehicleDTO vehicleDTO){
        try{
            vehicleService.updateVehicle(vehicleId,vehicleDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
