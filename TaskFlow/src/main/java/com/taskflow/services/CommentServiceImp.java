package com.taskflow.services;

import com.taskflow.dtos.requests.comments.CreateCommentRequest;
import com.taskflow.dtos.requests.comments.DetailCommentRequest;
import com.taskflow.dtos.requests.comments.ListCommentsRequest;
import com.taskflow.dtos.responses.comments.DetailCommentResponse;
import com.taskflow.exceptions.CommentNotFoundException;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.TaskNotFoundException;
import com.taskflow.models.CommentModel;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.TaskModel;
import com.taskflow.repositories.ICommentRepository;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.ITaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImp implements ICommentService{

    private final ITaskRepository taskRepository;
    private final IProjectRepository projectRepository;
    private final ICommentRepository commentRepository;

    public CommentServiceImp(ITaskRepository taskRepository, IProjectRepository projectRepository, ICommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void createComment(String userId, CreateCommentRequest commentRequest) {
        Optional<TaskModel> taskById = taskRepository.findById(commentRequest.getTaskId());
        if(!taskById.isPresent()){
            throw new TaskNotFoundException(commentRequest.getTaskId());
        }

        TaskModel taskExist = taskById.get();

        Optional<ProjectModel> projectById = projectRepository.findById(taskExist.getProjectId());
        if(!projectById.isPresent()){
            throw new ProjectNotFoundException(taskExist.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        Member targetMember = projectExist.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("El usuario " + userId + " no hace parte del proyecto. "));

        Instant date = Instant.now();
        CommentModel commentFinal = CommentModel.builder()
                .taskId(commentRequest.getTaskId())
                .authorId(userId)
                .content(commentRequest.getContent())
                .createdAt(date)
                .build();

        commentRepository.save(commentFinal);
    }

    @Override
    public DetailCommentResponse getComment(String userId, DetailCommentRequest commentRequest) {
        Optional<CommentModel> commentById = commentRepository.findById(commentRequest.getCommentId());
        if(!commentById.isPresent()){
            throw new CommentNotFoundException(commentRequest.getCommentId());
        }
        CommentModel commentExist = commentById.get();
        Optional<TaskModel> taskById = taskRepository.findById(commentExist.getTaskId());
        if(!taskById.isPresent()){
            throw new TaskNotFoundException(commentExist.getTaskId());
        }

        TaskModel taskExist = taskById.get();

        Optional<ProjectModel> projectById = projectRepository.findById(taskExist.getProjectId());
        if(!projectById.isPresent()){
            throw new ProjectNotFoundException(taskExist.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        Member targetMember = projectExist.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("El usuario " + userId + " no hace parte del proyecto. "));

        DetailCommentResponse commentResponse = DetailCommentResponse.builder()
                .commentId(commentExist.getId())
                .taskId(commentExist.getTaskId())
                .authorId(commentExist.getAuthorId())
                .content(commentExist.getContent())
                .createdAt(commentExist.getCreatedAt())
                .build();

        return commentResponse;
    }

    @Override
    public List<DetailCommentResponse> getComments(String userId, ListCommentsRequest commentRequest) {
        Optional<TaskModel> taskById = taskRepository.findById(commentRequest.getTaskId());
        if (!taskById.isPresent()) {
            throw new TaskNotFoundException(commentRequest.getTaskId());
        }

        TaskModel taskExist = taskById.get();

        Optional<ProjectModel> projectById = projectRepository.findById(taskExist.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(taskExist.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        projectExist.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("El usuario " + userId + " no hace parte del proyecto. "));

        List<CommentModel> comments = commentRepository.findByTaskId(commentRequest.getTaskId());

        return comments.stream()
                .map(comment -> DetailCommentResponse.builder()
                        .commentId(comment.getId())
                        .taskId(comment.getTaskId())
                        .authorId(comment.getAuthorId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
