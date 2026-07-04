package com.taskflow.dtos.requests.tasks;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class AssignTaskRequest {
    @NotBlank
    @Size(max = 50)
    private String taskId;
    @NotBlank
    @Size(max = 50)
    private String assignedTo;
}
