package com.example.cropMonitorSystem.secureAndResponse.secure;

import com.example.cropMonitorSystem.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUp {
    @Null(message = "Id generate by backend")
    private String user_id;
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    private Role role;
}
