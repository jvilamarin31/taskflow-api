package com.taskflow.models;

import com.taskflow.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private String userId;
    private RoleEnum role;
}
