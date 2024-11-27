package com.example.cropMonitorSystem.entity.impl;

import com.example.cropMonitorSystem.entity.SuperEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "crop")
public class CropEntity implements SuperEntity {
    @Id
    private String cropCode;
    private String cropName;
    private String scientificName;
    private String category;
    private String season;
    @Column(columnDefinition = "LONGTEXT")
    private String cropImage;
    @ManyToMany(mappedBy = "cropList")
    private List<LogEntity> logList;
    @ManyToMany(mappedBy = "cropList")
    private List<FieldEntity> fieldList;
}
