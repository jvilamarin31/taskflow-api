package com.taskflow.dtos.responses.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailCommentResponse {
    private String commentId;
    private String taskId;
    private String authorId;
    private String content;
    private Instant createdAt;
}
