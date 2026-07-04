package com.taskflow.dtos.requests.users;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    @NotNull
    @Email
    @Size(min = 3, max = 50)
    private String email;
    @NotBlank
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,20}$",
            message = "La contraseña debe contener al menos una letra, un número y un carácter especial")
    private String password;
    @NotBlank
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "El número debe tener entre 7 y 15 dígitos y puede incluir +"
    )
    private String mobilePhone;
}
