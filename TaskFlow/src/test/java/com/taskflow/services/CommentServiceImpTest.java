package com.taskflow.services;

import com.taskflow.dtos.requests.comments.CreateCommentRequest;
import com.taskflow.dtos.requests.comments.DeleteCommentRequest;
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
import com.taskflow.models.enums.RoleEnum;
import com.taskflow.repositories.ICommentRepository;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.ITaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImpTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private ICommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImp commentService;

    private final String userId = "user123";
    private final String taskId = "task456";
    private final String projectId = "proj789";
    private final String commentId = "comment001";

    private TaskModel createTask() {
        return TaskModel.builder()
                .id(taskId)
                .projectId(projectId)
                .title("Test Task")
                .build();
    }

    private ProjectModel createProjectWithMember(String memberId, RoleEnum role) {
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

    @Test
    void createComment_whenUserIsMember_shouldSaveComment() {
        //Given
        CreateCommentRequest request = new CreateCommentRequest(taskId, "Nice task!");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(
                Optional.of(createProjectWithMember(userId, RoleEnum.MEMBER))
        );

        //When
        commentService.createComment(userId, request);

        //Then
        verify(commentRepository, times(1)).save(any(CommentModel.class));
    }

    @Test
    void createComment_whenTaskNotFound_shouldThrowException() {
        //Given
        CreateCommentRequest request = new CreateCommentRequest(taskId, "Nice task!");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(TaskNotFoundException.class, () -> commentService.createComment(userId, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenProjectNotFound_shouldThrowException() {
        //Given
        CreateCommentRequest request = new CreateCommentRequest(taskId, "Nice task!");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> commentService.createComment(userId, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenUserIsNotMember_shouldThrowException() {
        //Given
        CreateCommentRequest request = new CreateCommentRequest(taskId, "Nice task!");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(
                Optional.of(createProjectWithMember("otherUser", RoleEnum.MEMBER))
        );

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> commentService.createComment(userId, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getComment_whenUserIsMember_shouldReturnComment() {
        //Given
        DetailCommentRequest request = new DetailCommentRequest(commentId);
        CommentModel comment = CommentModel.builder()
                .id(commentId)
                .taskId(taskId)
                .authorId(userId)
                .content("Nice task!")
                .createdAt(Instant.now())
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(userId, RoleEnum.MEMBER)));

        //When
        DetailCommentResponse response = commentService.getComment(userId, request);

        //Then
        assertNotNull(response);
        assertEquals(commentId, response.getCommentId());
        assertEquals("Nice task!", response.getContent());
    }

    @Test
    void getComment_whenCommentNotFound_shouldThrowException() {
        //Given
        DetailCommentRequest request = new DetailCommentRequest(commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(CommentNotFoundException.class, () -> commentService.getComment(userId, request));
    }

    @Test
    void getComments_whenUserIsMember_shouldReturnList() {
        //Given
        ListCommentsRequest request = new ListCommentsRequest(taskId);
        CommentModel comment1 = CommentModel.builder()
                .id("c1").taskId(taskId).authorId("u1").content("First!").build();
        CommentModel comment2 = CommentModel.builder()
                .id("c2").taskId(taskId).authorId("u2").content("Second!").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(
                Optional.of(createProjectWithMember(userId, RoleEnum.MEMBER))
        );
        when(commentRepository.findByTaskId(taskId)).thenReturn(List.of(comment1, comment2));

        //When
        List<DetailCommentResponse> responses = commentService.getComments(userId, request);

        //Then
        assertEquals(2, responses.size());
        assertEquals("First!", responses.get(0).getContent());
        assertEquals("Second!", responses.get(1).getContent());
    }

    @Test
    void DeleteComment_whenUserIsAuthor_shouldDelete() {
        //Given
        DeleteCommentRequest request = new DeleteCommentRequest(commentId);
        CommentModel comment = CommentModel.builder()
                .id(commentId)
                .taskId(taskId)
                .authorId(userId)
                .content("Bye!")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(userId, RoleEnum.MEMBER)));

        //When
        commentService.DeleteComment(userId, request);

        //Then
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void DeleteComment_whenUserIsAdminAndNotAuthor_shouldDelete() {
        //Given
        String adminId = "admin999";
        DeleteCommentRequest request = new DeleteCommentRequest(commentId);
        CommentModel comment = CommentModel.builder()
                .id(commentId)
                .taskId(taskId)
                .authorId(userId)
                .content("Bye!")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(adminId, RoleEnum.ADMIN)));

        //When
        commentService.DeleteComment(adminId, request);

        //Then
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void DeleteComment_whenUserIsNotAuthorAndNotAdmin_shouldThrowException() {
        //Given
        String otherUserId = "other999";
        DeleteCommentRequest request = new DeleteCommentRequest(commentId);
        CommentModel comment = CommentModel.builder()
                .id(commentId)
                .taskId(taskId)
                .authorId(userId)
                .content("Bye!")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(createTask()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember(otherUserId, RoleEnum.MEMBER)));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> commentService.DeleteComment(otherUserId, request));
        verify(commentRepository, never()).delete(any());
    }
}
