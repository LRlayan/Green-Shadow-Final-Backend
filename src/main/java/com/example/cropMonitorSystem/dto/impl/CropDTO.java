package com.example.cropMonitorSystem.dto.impl;

import com.example.cropMonitorSystem.dto.CropStatus;
import com.example.cropMonitorSystem.dto.SuperDTO;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CropDTO implements SuperDTO, CropStatus {
    @Id
    private String cropCode;
    private String cropName;
    private String scientificName;
    private String category;
    private String season;
    private String cropImage;
    private List<String> logCodeList;
    private List<String> fieldCodeList;
}
