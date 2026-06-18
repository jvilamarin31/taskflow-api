package com.taskflow.services;


import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.ProjectDeleteRequest;
import com.taskflow.dtos.requests.projects.ProjectDetailRequest;
import com.taskflow.dtos.requests.projects.ProjectUpdateRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.UserModel;
import com.taskflow.models.enums.RoleEnum;
import com.taskflow.models.enums.StatusProjectEnum;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImp implements IProjectService{

    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    public ProjectServiceImp(IProjectRepository projectRepository, IUserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createProject(String userId, CreateProjectRequest projectRequest) {
        Optional<UserModel> userById = userRepository.findById(userId);
        if(!userById.isPresent()){
            throw new UserNotFoundException(userId);
        }

        ProjectModel projectFinal = ProjectModel.builder()
                .name(projectRequest.getName())
                .description(projectRequest.getDescription())
                .ownerId(userId)
                .status(StatusProjectEnum.ACTIVE)
                .members(new ArrayList<>())
                .build();

        Member member = Member.builder()
                .userId(userId)
                .role(RoleEnum.OWNER)
                .build();

        projectFinal.getMembers().add(member);

        projectRepository.save(projectFinal);
    }

    @Override
    public List<ProjectModel> getProjectsByUser(String userId) {
        return List.of();
    }

    @Override
    public ProjectDetailResponse getProjectDetail(String userId, ProjectDetailRequest projectRequest) {
        return null;
    }

    @Override
    public void updateProject(String userId, ProjectUpdateRequest projectRequest) {

    }

    @Override
    public void deleteProject(String userId, ProjectDeleteRequest projectRequest) {

    }
}
