package com.example.cropMonitorSystem.dao;

import com.example.cropMonitorSystem.entity.impl.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentDAO extends JpaRepository<EquipmentEntity,String> {
    @Query(value = "SELECT * FROM equipment WHERE equipment_code = (SELECT equipment_code FROM equipment ORDER BY CAST(SUBSTRING(equipment_code, 11) AS UNSIGNED) DESC LIMIT 1);", nativeQuery = true)
    EquipmentEntity findLastRowNative();
}
