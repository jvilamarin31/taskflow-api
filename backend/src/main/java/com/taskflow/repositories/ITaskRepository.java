package com.taskflow.repositories;

import com.taskflow.models.TaskModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ITaskRepository extends MongoRepository<TaskModel, String> {
}
