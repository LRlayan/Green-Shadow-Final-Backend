package com.example.cropMonitorSystem.dto.impl;

import com.example.cropMonitorSystem.dto.FieldStatus;
import com.example.cropMonitorSystem.dto.SuperDTO;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FieldDTO implements SuperDTO, FieldStatus {
    @Id
    private String fieldCode;
    private String name;
    private String location;
    private double extentSize;
    private String fieldImage1;
    private String fieldImage2;
    private List<String> equipmentsList;
    private List<String> memberCodeList;
    private List<String> logCodeList;
    private List<String> cropCodeList;
}
