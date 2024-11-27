package com.example.cropMonitorSystem.dto.impl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String newPassword;
}
