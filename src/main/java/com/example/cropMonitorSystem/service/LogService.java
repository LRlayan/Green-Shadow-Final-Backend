package com.example.cropMonitorSystem.service;

import com.example.cropMonitorSystem.dto.LogStatus;
import com.example.cropMonitorSystem.dto.impl.LogDTO;

import java.util.List;

public interface LogService {
    void saveLog(LogDTO logDTO);
    List<LogDTO> getAllLog();
    void deleteLog(String id);
    void updateLog(String id, LogDTO logDTO);
    LogStatus getSelectedLog(String logId);
}
