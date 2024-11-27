package com.example.cropMonitorSystem.dao;

import com.example.cropMonitorSystem.entity.impl.FieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldDAO extends JpaRepository<FieldEntity,String> {
    @Query(value = "SELECT * FROM field WHERE field_code = (SELECT field_code FROM field ORDER BY CAST(SUBSTRING(field_code, 7) AS UNSIGNED) DESC LIMIT 1);", nativeQuery = true)
    FieldEntity findLastRowNative();
}
