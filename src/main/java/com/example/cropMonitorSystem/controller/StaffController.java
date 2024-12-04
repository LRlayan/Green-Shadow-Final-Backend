package com.example.cropMonitorSystem.controller;

import com.example.cropMonitorSystem.customStatusCode.SelectedErrorStatus;
import com.example.cropMonitorSystem.dto.StaffStatus;
import com.example.cropMonitorSystem.dto.impl.StaffDTO;
import com.example.cropMonitorSystem.exception.DataPersistException;
import com.example.cropMonitorSystem.exception.StaffNotFoundException;
import com.example.cropMonitorSystem.service.StaffService;
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
@RequestMapping("api/v1/staff")
@CrossOrigin
public class StaffController {
    @Autowired
    private StaffService staffService;
    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveStaff(@RequestBody StaffDTO staffDTO){
        try{
            if (!Regex.emailValidator(staffDTO.getEmail()).matches()){
                logger.error("Invalid staffId ID format: {}", staffDTO.getEmail());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            staffService.saveStaffMember(staffDTO);
            logger.info("Successfully saved staff with ID: {}", staffDTO.getMemberCode());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Data persist error while saving staff: {}", staffDTO, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while saving staff: {}", staffDTO, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @GetMapping(value = "/{staffId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public StaffStatus getSelectedStaff(@PathVariable("staffId") String staffId){
        if (!Regex.idValidator(staffId).matches()){
            logger.warn("Invalid staff ID format: {}", staffId);
            return new SelectedErrorStatus(1,"Staff Code Not Valid");
        }
        return staffService.getSelectedStaffMember(staffId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE','SCIENTIST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StaffDTO> getAllStaff(){
        try{
            List<StaffDTO> staff = staffService.getAllStaffMember();
            logger.info("Successfully retrieved all staff. Total count: {}", staff.size());
            return staff;
        }catch (Exception e){
            logger.error("Error occurred while fetching all staff", e);
            return null;
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @DeleteMapping(value = "/{staffId}")
    public ResponseEntity<Void> deleteStaff(@PathVariable ("staffId") String staffId){
        try{
            if (!Regex.idValidator(staffId).matches()){
                logger.warn("Invalid staffId ID format: {}", staffId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            staffService.deleteStaffMember(staffId);
            logger.info("Successfully deleted staff with ID: {}", staffId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (StaffNotFoundException e){
            logger.error("staff with ID {} not found for deletion", staffId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error("Error occurred while deleting staff with ID {}", staffId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMINISTRATIVE')")
    @PutMapping(value = "/{staffId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateStaff(@PathVariable ("staffId") String staffId ,@RequestBody StaffDTO staffDTO){
        try{
            staffService.updateStaffMember(staffId,staffDTO);
            logger.info("Successfully updated staff with ID: {}", staffId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (DataPersistException e){
            logger.error("Error occurred while updating staff with ID {}: Data persist error", staffId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Error occurred while updating staff with ID {}: Unexpected error", staffId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
