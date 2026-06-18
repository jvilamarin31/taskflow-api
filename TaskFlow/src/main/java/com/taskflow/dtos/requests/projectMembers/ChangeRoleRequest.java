package com.taskflow.dtos.requests.projectMembers;

import com.taskflow.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    private String projectId;
    private String memberId;
    private RoleEnum role;
}
