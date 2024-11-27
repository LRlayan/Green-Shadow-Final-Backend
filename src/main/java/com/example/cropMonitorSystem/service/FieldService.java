package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.FieldStatus;
import com.example.cropMonitorSystem.dto.impl.FieldDTO;
import com.example.cropMonitorSystem.exception.FieldNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FieldService {
    void saveField(FieldDTO fieldDTO);
    List<FieldDTO> getAllField() throws IOException, ClassNotFoundException;
    void deleteField(String id) throws FileNotFoundException, FieldNotFoundException;
    void updateField(String id,FieldDTO fieldDTO);
    FieldStatus getSelectedField(String fieldId);
}
