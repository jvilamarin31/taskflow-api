package com.taskflow.dtos.responses.tasks;

import com.taskflow.models.enums.PriorityEnum;
import com.taskflow.models.enums.StatusTaskEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDetailResponse {
    private String taskId;
    private String projectId;
    private String title;
    private String description;
    private String createdBy;
    private String assignedTo;
    private StatusTaskEnum status;
    private PriorityEnum priority;
    private Instant dueDate;
    private Instant createdAt;
}
