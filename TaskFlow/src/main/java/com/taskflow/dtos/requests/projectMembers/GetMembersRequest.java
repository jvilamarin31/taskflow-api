package com.taskflow.dtos.requests.projectMembers;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMembersRequest {
    @Size(max = 50)
    private String projectId;
}
