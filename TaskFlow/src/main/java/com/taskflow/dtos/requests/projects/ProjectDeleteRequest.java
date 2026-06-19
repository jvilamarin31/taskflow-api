package com.taskflow.dtos.requests.projects;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDeleteRequest {
    @Size(max = 50)
    private String projectId;
}
