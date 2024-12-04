package com.example.cropMonitorSystem.dao;

import com.example.cropMonitorSystem.entity.impl.CropEntity;
import com.example.cropMonitorSystem.entity.impl.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDAO extends JpaRepository<VehicleEntity,String> {
    @Query(value = "SELECT * FROM vehicle WHERE vehicle_code = (SELECT vehicle_code FROM vehicle ORDER BY CAST(SUBSTRING(vehicle_code, 9) AS UNSIGNED) DESC LIMIT 1);", nativeQuery = true)
    VehicleEntity findLastRowNative();
    @Query(value = "SELECT * FROM vehicle ORDER BY CAST(SUBSTRING(vehicle_code, 9) AS UNSIGNED);", nativeQuery = true)
    @Override
    List<VehicleEntity> findAll();
}
