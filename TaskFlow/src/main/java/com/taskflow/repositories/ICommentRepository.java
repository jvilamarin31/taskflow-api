package com.taskflow.repositories;

import com.taskflow.models.CommentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICommentRepository extends MongoRepository<CommentModel, String> {
    List<CommentModel> findByTaskId(String taskId);
}
