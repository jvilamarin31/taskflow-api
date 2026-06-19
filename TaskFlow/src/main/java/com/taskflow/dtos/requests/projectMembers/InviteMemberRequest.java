package com.taskflow.dtos.requests.projectMembers;

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
public class InviteMemberRequest {
    @Size(max = 50)
    private String projectId;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @Size(max = 50)
    @Pattern(
            regexp = "^(ADMIN|MEMBER)$",
            message = "Rol no válido. Valores permitidos: ADMIN, MEMBER"
    )
    private String role;
}
