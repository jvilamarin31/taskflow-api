package com.taskflow.dtos.responses.projects;

import com.taskflow.models.Member;
import com.taskflow.models.enums.StatusProjectEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDetailResponse {
    private String projectId;
    private String name;
    private String description;
    private String ownerId;
    private StatusProjectEnum status;
    private ArrayList<Member> members;
}
