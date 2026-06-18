package com.taskflow.dtos.responses.projectMembers;

import com.taskflow.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailResponse {
    private String userId;
    private String name;
    private String email;
    private String mobilePhone;
    private RoleEnum ROLE;
}
