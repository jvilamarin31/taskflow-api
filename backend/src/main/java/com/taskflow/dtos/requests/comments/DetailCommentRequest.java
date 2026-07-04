package com.taskflow.dtos.requests.comments;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class DetailCommentRequest {
    private String commentId;
}
