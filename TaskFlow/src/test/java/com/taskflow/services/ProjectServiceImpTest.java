package com.taskflow.services;

import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.DeleteProjectRequest;
import com.taskflow.dtos.requests.projects.DetailProjectRequest;
import com.taskflow.dtos.requests.projects.UpdateProjectRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.UserModel;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImpTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ProjectServiceImp projectService;

    private String userId = "user123";
    private String projectId = "proj789";

    private UserModel createUser() {
        return UserModel.builder()
                .id(userId)
                .name("name")
                .email("email@gmail.com")
                .mobilePhone("3005004521")
                .build();
    }

    private ProjectModel createProjectWithoutMember(String ownerId) {
        return ProjectModel.builder()
                .id(projectId)
                .name("Test Project")
                .ownerId(ownerId)
                .build();
    }

    private ProjectModel createProject(String ownerId, String projectId) {
        return ProjectModel.builder()
                .id(projectId)
                .name("Test Project " + projectId)
                .description("Description " + projectId)
                .ownerId(ownerId)
                .status(com.taskflow.models.enums.StatusProjectEnum.ACTIVE)
                .members(new ArrayList<>())
                .build();
    }

    @Test
    void createProject_whenUserExist_shouldSaveProject() {
        //Given
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test projectName");
        request.setDescription("Test projectDescription");

        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));

        //When
        projectService.createProject(userId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void createProject_whenUserNotFound_shouldThrowException() {
        //Given
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test projectName");
        request.setDescription("Test projectDescription");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(UserNotFoundException.class, () -> projectService.createProject(userId, request));
    }

    @Test
    void getProjectsByUser_whenUserNotFound_shouldThrowException() {
        //Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(UserNotFoundException.class, () -> projectService.getProjectsByUser(userId));
    }

    @Test
    void getProjectsByUser_whenUserHasProjects_shouldReturnList() {
        //Given
        ProjectModel project1 = createProject(userId, "proj1");
        ProjectModel project2 = createProject(userId, "proj2");

        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        when(projectRepository.findProjectsByUserId(userId)).thenReturn(List.of(project1, project2));

        //When
        List<ProjectDetailResponse> response = projectService.getProjectsByUser(userId);

        //Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Test Project proj1", response.get(0).getName());
        assertEquals("Test Project proj2", response.get(1).getName());
        verify(projectRepository, times(1)).findProjectsByUserId(userId);
    }

    @Test
    void getProjectsByUser_whenUserHasNoProjects_shouldReturnEmptyList() {
        //Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        when(projectRepository.findProjectsByUserId(userId)).thenReturn(List.of());

        //When
        List<ProjectDetailResponse> response = projectService.getProjectsByUser(userId);

        //Then
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getProjectDetail_whenProjectExist_shouldGetProjectDetail() {
        //Given
        DetailProjectRequest request = new DetailProjectRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithoutMember(userId)));

        //When
        ProjectDetailResponse response = projectService.getProjectDetail(request);

        //Then
        assertNotNull(response);
        assertEquals(projectId, response.getProjectId());
        assertEquals("Test Project", response.getName());
    }

    @Test
    void getProjectDetail_whenProjectNotFound_shouldThrowException() {
        //Given
        DetailProjectRequest request = new DetailProjectRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectDetail(request));
    }

    @Test
    void updateProject_whenUserIsOwner_shouldUpdateProject() {
        //Given
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setProjectId(projectId);
        request.setName("Test updateName");
        request.setDescription("Test updateDescription");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithoutMember(userId)));

        //When
        projectService.updateProject(userId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void updateProject_whenUserIsNotOwner_shouldThrowException() {
        //Given
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setProjectId(projectId);
        request.setName("Test updateName");
        request.setDescription("Test updateDescription");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithoutMember("1234")));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectService.updateProject(userId, request));
    }

    @Test
    void deleteProject_whenUserIsOwner_shouldDeleteProject() {
        //Given
        DeleteProjectRequest request = new DeleteProjectRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithoutMember(userId)));

        //When
        projectService.deleteProject(userId, request);

        //then
        verify(projectRepository, times(1)).delete(any(ProjectModel.class));
    }

    @Test
    void deleteProject_whenProjectNotFound_shouldThrowException() {
        //Given
        DeleteProjectRequest request = new DeleteProjectRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(userId, request));
    }

    @Test
    void deleteProject_whenUserIsNotOwner_shouldThrowException() {
        //Given
        DeleteProjectRequest request = new DeleteProjectRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithoutMember("1234")));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectService.deleteProject(userId, request));

    }
}
