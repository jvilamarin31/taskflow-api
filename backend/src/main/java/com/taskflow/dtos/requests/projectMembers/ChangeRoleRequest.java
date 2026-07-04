package com.taskflow.dtos.requests.projectMembers;

import com.taskflow.models.enums.RoleEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    @Size(max = 50)
    private String projectId;
    @Size(max = 50)
    private String memberId;
    @NotBlank
    @Size(max = 50)
    @Pattern(
            regexp = "^(ADMIN|MEMBER)$",
            message = "Rol no válido. Valores permitidos: ADMIN, MEMBER"
    )
    private String role;
}
