package com.example.cropMonitorSystem.entity.impl;

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
@Table(name = "logs")
public class LogEntity {
    @Id
    private String logCode;
    private String date;
    private String logDetails;
    @Column(columnDefinition = "LONGTEXT")
    private String observedImage;
    @ManyToMany
    @JoinTable(
            name = "staff_log_details",
            joinColumns = @JoinColumn(name = "logCode"),
            inverseJoinColumns = @JoinColumn(name = "memberCode")
    )
    private List<StaffEntity> staffList;
    @ManyToMany
    @JoinTable(
            name = "log_crop_details",
            joinColumns = @JoinColumn(name = "logCode"),
            inverseJoinColumns = @JoinColumn(name = "cropCode")
    )
    private List<CropEntity> cropList;
    @ManyToMany
    @JoinTable(
            name = "field_log_details",
            joinColumns = @JoinColumn(name = "logCode"),
            inverseJoinColumns = @JoinColumn(name = "fieldCode")
    )
    private List<FieldEntity> fieldList;
}
