package com.taskflow.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("Comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentModel {
    @Id
    private String id;
    private String taskId;
    private String authorId;
    private String content;
    private Instant createdAt;
}
