package com.taskflow.services;

import com.taskflow.dtos.requests.tasks.*;
import com.taskflow.dtos.responses.tasks.TaskDetailResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.TaskNotFoundException;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.TaskModel;
import com.taskflow.models.enums.PriorityEnum;
import com.taskflow.models.enums.RoleEnum;
import com.taskflow.models.enums.StatusTaskEnum;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.ITaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceImpTest {

    @Mock
    private IProjectRepository projectRepository;
    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TaskServiceImp taskService;

    private final String userId = "user123";
    private final String taskId = "task456";
    private final String projectId = "proj789";

    private ProjectModel createProjectWithMember(String projectId, String memberId, RoleEnum role) {
        Member member = Member.builder()
                .userId(memberId)
                .role(role)
                .build();
        return ProjectModel.builder()
                .id(projectId)
                .name("Test Project")
                .members(new ArrayList<>(List.of(member)))
                .build();
    }

    private TaskModel createTask(String assignedId) {
        TaskModel task = TaskModel.builder()
                .id(taskId)
                .projectId(projectId)
                .title("Test Task")
                .description("Test Description")
                .createdBy(userId)
                .status(StatusTaskEnum.TO_DO)
                .priority(PriorityEnum.LOW)
                .dueDate(Instant.parse("2040-01-31T00:00:00Z"))
                .build();
        if (assignedId != null && !assignedId.isEmpty()) {
            task.setAssignedTo(assignedId);
        }
        return task;
    }

    @Test
    void createTask_whenUserIsMember_shouldSaveTask() {
        //Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("LOW");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(projectRepository.findById(request.getProjectId())).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When
        taskService.createTask(userId, request);

        //Then
        verify(taskRepository,times(1)).save(any(TaskModel.class));
    }

    @Test
    void createTask_whenUserIsNotMember_shouldThrowException() {
        //Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("LOW");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(projectRepository.findById(request.getProjectId())).thenReturn(Optional.of(createProjectWithMember(projectId, "12345", RoleEnum.MEMBER)));

        //When
        assertThrows(InvalidCredentialsException.class,() -> taskService.createTask(userId, request));

        //Then
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_whenProjectNotFound_shouldThrowException() {
        //Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(projectId);
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("LOW");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(projectRepository.findById(request.getProjectId())).thenReturn(Optional.empty());

        //When
        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(projectId, request));

        //Then
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTask_whenUserIsMember_shouldGetTask() {
        //Given
        TaskDetailRequest request = new TaskDetailRequest(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask(userId)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When
        TaskDetailResponse response = taskService.getTask(userId, request);

        //Then
        assertNotNull(response);
        assertEquals(taskId, response.getTaskId());
        assertEquals(projectId, response.getProjectId());
    }

    @Test
    void getTask_whenUserIsNotMember_shouldThrowException() {
        //Given
        TaskDetailRequest request = new TaskDetailRequest(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask(userId)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.getTask("1234", request));
    }

    @Test
    void getTask_whenProjectNotFound_shouldThrowException() {
        TaskDetailRequest request = new TaskDetailRequest(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask(userId)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> taskService.getTask(userId, request));
    }

    @Test
    void getTask_whenTaskNotFound_shouldThrowException() {
        //Given
        TaskDetailRequest request = new TaskDetailRequest(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(TaskNotFoundException.class, () -> taskService.getTask(userId, request));
    }

    @Test
    void updateTask_whenUserIsMember_shouldUpdateTask() {
        //Given
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskId(taskId);
        request.setTitle("Test updateTitle");
        request.setDescription("Test updateDescription");
        request.setStatus("IN_PROGRESS");
        request.setPriority("HIGH");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When
        taskService.updateTask(userId, request);

        //Then
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_whenTaskNotFound_shouldThrowException() {
        //Given
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskId(taskId);
        request.setTitle("Test updateTitle");
        request.setDescription("Test updateDescription");
        request.setStatus("IN_PROGRESS");
        request.setPriority("HIGH");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(userId, request));
    }

    @Test
    void updateTask_whenProjectNotFound_shouldThrowException() {
        //Given
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskId(taskId);
        request.setTitle("Test updateTitle");
        request.setDescription("Test updateDescription");
        request.setStatus("IN_PROGRESS");
        request.setPriority("HIGH");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask(userId)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> taskService.updateTask(userId, request));
    }

    @Test
    void updateTask_whenUserIsNotMember_shouldThrowException() {
        //Given
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskId(taskId);
        request.setTitle("Test updateTitle");
        request.setDescription("Test updateDescription");
        request.setStatus("IN_PROGRESS");
        request.setPriority("HIGH");
        request.setDueDate(Instant.parse("2060-01-31T00:00:00Z"));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask(userId)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, "1234", RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.updateTask(userId, request));
    }

    @Test
    void assignTask_whenUserIsOwner_shouldAssignTask() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo(userId);

        TaskModel task = createTask(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.OWNER)));

        //When
        taskService.assignTask(userId, request);

        //Then
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void assignTask_whenUserIsAdmin_shouldAssignTask() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo(userId);

        TaskModel task = createTask(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.ADMIN)));

        //When
        taskService.assignTask(userId, request);

        //Then
        verify(taskRepository, times(1)).save(any(TaskModel.class));
    }

    @Test
    void assignTask_whenUserIsNotMember_shouldThrowException() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo("1234");

        TaskModel task = createTask(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.OWNER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.assignTask(userId, request));
    }

    @Test
    void assignTask_whenTaskNotFound_shouldThrowException() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo(userId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        //When y Then
        assertThrows(TaskNotFoundException.class, () -> taskService.assignTask(userId, request));
    }

    @Test
    void assignTask_whenProjectNotFound_shouldThrowException() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo(userId);

        TaskModel task = createTask(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> taskService.assignTask(projectId, request));
    }

    @Test
    void assignTask_whenUserIsNotOwnerOrAdmin_shouldThrowException() {
        //Given
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAssignedTo(userId);

        TaskModel task = createTask(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.assignTask(projectId, request));
    }

    @Test
    void deleteTask_whenUserIsOwner_shouldDeleteTask() {
        //Given
        DeleteTaskRequest request = new DeleteTaskRequest();
        request.setTaskId(taskId);

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.OWNER)));

        //When
        taskService.deleteTask(userId, request);

        //Then
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_whenUserIsAdmin_shouldDeleteTask() {
        //Given
        DeleteTaskRequest request = new DeleteTaskRequest();
        request.setTaskId(taskId);

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.ADMIN)));

        //When
        taskService.deleteTask(userId, request);

        //Then
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_whenUserIsMember_shouldThrowException() {
        //Given
        DeleteTaskRequest request = new DeleteTaskRequest();
        request.setTaskId(taskId);

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, userId, RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.deleteTask(userId, request));
    }

    @Test
    void deleteTask_whenUserIsNotMember_shouldThrowException() {
        //Given
        DeleteTaskRequest request = new DeleteTaskRequest();
        request.setTaskId(taskId);

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(projectId, "1234", RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> taskService.deleteTask(userId, request));
    }

    @Test
    void deleteTask_whenTaskNotFound_shouldThrowException() {
        //Given
        DeleteTaskRequest request = new DeleteTaskRequest();
        request.setTaskId(taskId);

        TaskModel task = createTask(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> taskService.deleteTask(userId, request));
    }
}
