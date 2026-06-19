package com.taskflow.services;

import com.taskflow.dtos.requests.tasks.CreateTaskRequest;
import com.taskflow.dtos.requests.tasks.ListTasksRequest;
import com.taskflow.dtos.requests.tasks.TaskDetailRequest;
import com.taskflow.dtos.responses.tasks.TaskDetailResponse;
import com.taskflow.exceptions.InvalidInvitationException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.TaskNotFoundException;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.TaskModel;
import com.taskflow.models.enums.PriorityEnum;
import com.taskflow.models.enums.StatusTaskEnum;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.ITaskRepository;
import com.taskflow.repositories.IUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImp implements ITaskService{
    private final IUserRepository userRepository;
    private final IProjectRepository projectRepository;
    private final ITaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    public TaskServiceImp(IUserRepository userRepository, IProjectRepository projectRepository, ITaskRepository taskRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void createTask(String userId, CreateTaskRequest taskRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(taskRequest.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(taskRequest.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        Member targetMember = projectExist.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidInvitationException("El usuario " + userId + " no es miembro del proyecto. "));

        PriorityEnum priorityEnum = PriorityEnum.valueOf(taskRequest.getPriority());

        TaskModel task = TaskModel.builder()
                .projectId(taskRequest.getProjectId())
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .createdBy(userId)
                .status(StatusTaskEnum.TO_DO)
                .priority(priorityEnum)
                .dueDate(taskRequest.getDueDate())
                .createdAt(Instant.now())
                .build();
        if (taskRequest.getAssignedTo() != null && !taskRequest.getAssignedTo().isBlank()) {
            Member memberAssignedTo = projectExist.getMembers().stream()
                    .filter(member -> member.getUserId().equals(taskRequest.getAssignedTo()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInvitationException("El usuario con id:" + taskRequest.getAssignedTo() + " al que se le quiere asignar la tarea no es miembro del proyecto."));
            task.setAssignedTo(memberAssignedTo.getUserId());
        }

        taskRepository.save(task);
    }

    @Override
    public TaskDetailResponse getTask(String userId, TaskDetailRequest taskRequest) {
        Optional<TaskModel> taskById = taskRepository.findById(taskRequest.getTaskId());
        if (!taskById.isPresent()) {
            throw new TaskNotFoundException(taskRequest.getTaskId());
        }
        TaskModel taskExist = taskById.get();

        Optional<ProjectModel> projectById = projectRepository.findById(taskExist.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(taskExist.getProjectId());
        }
        ProjectModel projectExist = projectById.get();
        Member targetMember = projectExist.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidInvitationException("El usuario " + userId + " no es miembro del proyecto. "));

        TaskDetailResponse taskResponse = TaskDetailResponse.builder()
                .taskId(taskExist.getId())
                .projectId(projectExist.getId())
                .title(taskExist.getTitle())
                .description(taskExist.getDescription())
                .createdBy(taskExist.getCreatedBy())
                .status(taskExist.getStatus())
                .priority(taskExist.getPriority())
                .dueDate(taskExist.getDueDate())
                .createdAt(taskExist.getCreatedAt())
                .build();
        if (taskExist.getAssignedTo() != null && !taskExist.getAssignedTo().isBlank()) {
            taskResponse.setAssignedTo(taskExist.getAssignedTo());
        }

        return taskResponse;
    }

    @Override
    public List<TaskDetailResponse> getTasks(String userId, ListTasksRequest request) {
        Optional<ProjectModel> projectById = projectRepository.findById(request.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(request.getProjectId());
        }
        ProjectModel project = projectById.get();

        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (!isMember) {
            throw new InvalidInvitationException("El usuario " + userId + " no es miembro del proyecto.");
        }

        Criteria criteria = Criteria.where("projectId").is(request.getProjectId());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            criteria = criteria.and("status").is(StatusTaskEnum.valueOf(request.getStatus()));
        }
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            criteria = criteria.and("priority").is(PriorityEnum.valueOf(request.getPriority()));
        }
        if (request.getAssignedTo() != null && !request.getAssignedTo().isBlank()) {
            criteria = criteria.and("assignedTo").is(request.getAssignedTo());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            criteria = criteria.and("title").regex(".*" + request.getTitle() + ".*", "i");
        }

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Query query = new Query(criteria)
                .with(PageRequest.of(request.getPage(), request.getSize(), sort));

        List<TaskModel> tasks = mongoTemplate.find(query, TaskModel.class);

        return tasks.stream()
                .map(t -> TaskDetailResponse.builder()
                        .taskId(t.getId())
                        .projectId(t.getProjectId())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .createdBy(t.getCreatedBy())
                        .assignedTo(t.getAssignedTo())
                        .status(t.getStatus())
                        .priority(t.getPriority())
                        .dueDate(t.getDueDate())
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
