package com.taskflow.dtos.requests.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,20}$",
            message = "La contraseña debe contener al menos una letra, un número y un carácter especial")
    private String password;
}
