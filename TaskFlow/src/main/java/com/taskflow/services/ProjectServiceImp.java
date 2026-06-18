package com.taskflow.services;


import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.ProjectDeleteRequest;
import com.taskflow.dtos.requests.projects.ProjectDetailRequest;
import com.taskflow.dtos.requests.projects.ProjectUpdateRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.ProjectNotFoundException;
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
    public List<ProjectDetailResponse> getProjectsByUser(String userId) {
        Optional<UserModel> userById = userRepository.findById(userId);
        if (!userById.isPresent()) {
            throw new UserNotFoundException(userId);
        }
        return projectRepository.findProjectsByUserId(userId).stream()
                .map(p -> ProjectDetailResponse.builder()
                        .projectId(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .ownerId(p.getOwnerId())
                        .status(p.getStatus())
                        .members(p.getMembers())
                        .build())
                .toList();
    }

    @Override
    public ProjectDetailResponse getProjectDetail(ProjectDetailRequest projectRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(projectRequest.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(projectRequest.getProjectId());
        }

        ProjectModel projectExist = projectById.get();

        ProjectDetailResponse ProjectResponse = ProjectDetailResponse.builder()
                .projectId(projectExist.getId())
                .name(projectExist.getName())
                .description(projectExist.getDescription())
                .ownerId(projectExist.getOwnerId())
                .status(projectExist.getStatus())
                .members(projectExist.getMembers())
                .build();

        return ProjectResponse;

    }

    @Override
    public void updateProject(String userId, ProjectUpdateRequest projectRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(projectRequest.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(projectRequest.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        if (!projectExist.getOwnerId().equals(userId)){
            throw new InvalidCredentialsException("Solo el propietario del proyecto puede actualizarlo. ");
        }

        projectExist.setName(projectRequest.getName());
        projectExist.setDescription(projectRequest.getDescription());

        projectRepository.save(projectExist);
    }

    @Override
    public void deleteProject(String userId, ProjectDeleteRequest projectRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(projectRequest.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(projectRequest.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        if (!projectExist.getOwnerId().equals(userId)){
            throw new InvalidCredentialsException("Solo el propietario del proyecto puede actualizarlo. ");
        }

        projectRepository.delete(projectExist);
    }
}
