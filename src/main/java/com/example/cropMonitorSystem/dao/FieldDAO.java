package com.example.cropMonitorSystem.dao;

import com.example.cropMonitorSystem.entity.impl.CropEntity;
import com.example.cropMonitorSystem.entity.impl.FieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldDAO extends JpaRepository<FieldEntity,String> {
    @Query(value = "SELECT * FROM field WHERE field_code = (SELECT field_code FROM field ORDER BY CAST(SUBSTRING(field_code, 7) AS UNSIGNED) DESC LIMIT 1);", nativeQuery = true)
    FieldEntity findLastRowNative();
    @Query(value = "SELECT * FROM field ORDER BY CAST(SUBSTRING(field_code, 7) AS UNSIGNED);", nativeQuery = true)
    @Override
    List<FieldEntity> findAll();
}
