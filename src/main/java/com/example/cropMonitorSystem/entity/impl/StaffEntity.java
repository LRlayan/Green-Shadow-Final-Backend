package com.example.cropMonitorSystem.entity.impl;

import com.example.cropMonitorSystem.entity.Gender;
import com.example.cropMonitorSystem.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff")
public class StaffEntity {
    @Id
    private String memberCode;
    private String firstName;
    private String lastName;
    private LocalDate joinedDate;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String designation;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String addressLine5;
    private String contactNo;
    @Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "staff")
    private List<VehicleEntity> vehicleList;
    @ManyToMany(mappedBy = "staffList")
    private List<FieldEntity> fieldList;
    @ManyToMany(mappedBy = "staffList")
    private List<LogEntity> logList;
    @ManyToMany(mappedBy = "staffCodeList")
    private List<EquipmentEntity> equipmentList;
}
