package com.taskflow.dtos.requests.projects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateRequest {
    private String projectId;
    private String name;
    private String description;
}
