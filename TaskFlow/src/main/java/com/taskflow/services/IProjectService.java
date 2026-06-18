package com.taskflow.services;

import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.ProjectDeleteRequest;
import com.taskflow.dtos.requests.projects.ProjectDetailRequest;
import com.taskflow.dtos.requests.projects.ProjectUpdateRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;
import com.taskflow.models.ProjectModel;

import java.util.List;

public interface IProjectService {
    public void createProject(String userId, CreateProjectRequest projectRequest);
    public List<ProjectDetailResponse> getProjectsByUser(String userId);
    public ProjectDetailResponse getProjectDetail(ProjectDetailRequest projectRequest);
    public void updateProject(String userId, ProjectUpdateRequest projectRequest);
    public void deleteProject(String userId, ProjectDeleteRequest projectRequest);
}
