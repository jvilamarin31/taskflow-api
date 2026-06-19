package com.taskflow.dtos.requests.projects;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateRequest {
    @Size(max = 50)
    private String projectId;
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;
}
