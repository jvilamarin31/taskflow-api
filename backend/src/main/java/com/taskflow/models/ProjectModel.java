package com.taskflow.models;

import com.taskflow.models.enums.StatusProjectEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document("Projects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectModel {
    @Id
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private StatusProjectEnum status;
    private ArrayList<Member> members;
}
