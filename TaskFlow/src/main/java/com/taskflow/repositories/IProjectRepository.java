package com.taskflow.repositories;

import com.taskflow.models.ProjectModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IProjectRepository extends MongoRepository<ProjectModel, String> {
}
