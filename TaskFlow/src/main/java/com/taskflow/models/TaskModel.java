package com.taskflow.models;

import com.taskflow.models.enums.PriorityEnum;
import com.taskflow.models.enums.StatusTaskEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("Tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel {
    @Id
    private String id;
    private String projectId;
    private String title;
    private String description;
    private String createdBy;
    private String assignedTo;
    private StatusTaskEnum status;
    private PriorityEnum priority;
    private Instant dueDate;
}
