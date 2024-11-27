package com.example.cropMonitorSystem.dto.impl;

import com.example.cropMonitorSystem.dto.LogStatus;
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
public class LogDTO implements SuperDTO , LogStatus {
    @Id
    private String logCode;
    private String date;
    private String logDetails;
    private String observedImage;
    private List<String> staffList;
    private List<String> cropList;
    private List<String> fieldList;
}
