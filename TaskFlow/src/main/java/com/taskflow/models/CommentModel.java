package com.taskflow.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel {
    @Id
    private String id;
    private String taskId;
    private String authorId;
    private String content;
}
