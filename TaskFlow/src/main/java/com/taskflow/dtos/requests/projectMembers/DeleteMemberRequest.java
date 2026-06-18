package com.taskflow.dtos.requests.projectMembers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMemberRequest {
    private String projectId;
    private String memberId;
}
