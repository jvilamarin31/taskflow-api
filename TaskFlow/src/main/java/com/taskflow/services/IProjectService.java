package com.taskflow.services;

import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.DeleteProjectRequest;
import com.taskflow.dtos.requests.projects.DetailProjectRequest;
import com.taskflow.dtos.requests.projects.UpdateProjectRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;

import java.util.List;

public interface IProjectService {
    public void createProject(String userId, CreateProjectRequest projectRequest);
    public List<ProjectDetailResponse> getProjectsByUser(String userId);
    public ProjectDetailResponse getProjectDetail(DetailProjectRequest projectRequest);
    public void updateProject(String userId, UpdateProjectRequest projectRequest);
    public void deleteProject(String userId, DeleteProjectRequest projectRequest);
}
