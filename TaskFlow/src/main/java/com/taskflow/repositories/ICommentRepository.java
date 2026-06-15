package com.taskflow.repositories;

import com.taskflow.models.CommentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICommentRepository extends MongoRepository<CommentModel, String> {
}
