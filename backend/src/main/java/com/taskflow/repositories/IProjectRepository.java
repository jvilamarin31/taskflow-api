package com.taskflow.repositories;

import com.taskflow.models.ProjectModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IProjectRepository extends MongoRepository<ProjectModel, String> {
    @Query("{ 'members.userId': ?0 }")
    List<ProjectModel> findProjectsByUserId(String userId);
    Optional<ProjectModel> findById(String projectId);
}
