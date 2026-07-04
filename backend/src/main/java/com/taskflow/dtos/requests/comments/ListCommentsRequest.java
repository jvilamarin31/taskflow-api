package com.taskflow.dtos.requests.comments;

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
public class ListCommentsRequest {
    @NotBlank
    @Size(max = 50)
    private String taskId;
}
