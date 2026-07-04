package com.taskflow.services;

import com.taskflow.dtos.requests.Invitations.InviteMemberRequest;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.InvalidInvitationException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.jwts.JwtService;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.UserModel;
import com.taskflow.models.enums.RoleEnum;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.IUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceImpTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private InvitationServiceImp invitationService;

    private final String adminUserId = "admin123";
    private final String ownerUserId = "owner123";
    private final String memberUserId = "member456";
    private final String projectId = "proj789";
    private final String invitedEmail = "invited@email.com";
    private final String invitedUserId = "invited999";
    private final String token = "mocked-invitation-token";

    private ProjectModel createProjectWithMembers() {
        Member owner = Member.builder().userId(ownerUserId).role(RoleEnum.OWNER).build();
        Member admin = Member.builder().userId(adminUserId).role(RoleEnum.ADMIN).build();
        Member member = Member.builder().userId(memberUserId).role(RoleEnum.MEMBER).build();
        ProjectModel project = ProjectModel.builder()
                .id(projectId)
                .name("Test Project")
                .ownerId(ownerUserId)
                .members(new ArrayList<>(List.of(owner)))
                .build();
        project.getMembers().add(admin);
        project.getMembers().add(member);
        return project;
    }

    private UserModel createInvitedUser() {
        return UserModel.builder()
                .id(invitedUserId)
                .email(invitedEmail)
                .name("Invited User")
                .mobilePhone("3000000000")
                .build();
    }

    private void setBaseUrl(String url) throws Exception {
        Field field = InvitationServiceImp.class.getDeclaredField("baseUrl");
        field.setAccessible(true);
        field.set(invitationService, url);
    }

    @Test
    void inviteMember_whenAdminInvites_shouldSendEmail() throws Exception {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "MEMBER");
        MimeMessage mimeMessage = mock(MimeMessage.class);

        setBaseUrl("http://localhost:8080");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(createInvitedUser()));
        when(jwtService.getInvitationToken(invitedEmail, projectId, "MEMBER")).thenReturn(token);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        invitationService.inviteMember(adminUserId, request);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void inviteMember_whenOwnerInvites_shouldSendEmail() throws Exception {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "ADMIN");
        MimeMessage mimeMessage = mock(MimeMessage.class);

        setBaseUrl("http://localhost:8080");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(createInvitedUser()));
        when(jwtService.getInvitationToken(invitedEmail, projectId, "ADMIN")).thenReturn(token);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        invitationService.inviteMember(ownerUserId, request);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void inviteMember_whenProjectNotFound_shouldThrowException() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "MEMBER");
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> invitationService.inviteMember(adminUserId, request));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void inviteMember_whenUserIsNotMember_shouldThrowException() {
        // Given
        String nonMemberId = "nonMember999";
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "MEMBER");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> invitationService.inviteMember(nonMemberId, request));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void inviteMember_whenUserIsMemberWithoutPermission_shouldThrowException() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "MEMBER");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> invitationService.inviteMember(memberUserId, request));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void inviteMember_whenInvitedEmailNotFound_shouldThrowException() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, "unknown@email.com", "MEMBER");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> invitationService.inviteMember(adminUserId, request));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void inviteMember_whenUserAlreadyMember_shouldThrowException() {
        // Given
        InviteMemberRequest request = new InviteMemberRequest(projectId, invitedEmail, "MEMBER");
        UserModel invitedUser = createInvitedUser();

        ProjectModel project = createProjectWithMembers();
        project.getMembers().add(Member.builder().userId(invitedUserId).role(RoleEnum.MEMBER).build());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(invitedUser));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> invitationService.inviteMember(adminUserId, request));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void acceptInvitation_whenTokenIsValid_shouldAddMember() {
        // Given
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(invitedEmail);
        when(claims.get("projectId", String.class)).thenReturn(projectId);
        when(claims.get("role", String.class)).thenReturn("MEMBER");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.getAllClaims(token)).thenReturn(claims);
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(createInvitedUser()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMembers()));

        // When
        String result = invitationService.acceptInvitation(token);

        // Then
        assertEquals("Invitación aceptada. Ahora eres miembro del proyecto.", result);
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void acceptInvitation_whenTokenExpired_shouldThrowException() {
        // Given
        when(jwtService.isTokenValid(token)).thenReturn(false);

        // When & Then
        assertThrows(InvalidInvitationException.class, () -> invitationService.acceptInvitation(token));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void acceptInvitation_whenUserNotFound_shouldThrowException() {
        // Given
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(invitedEmail);
        when(claims.get("projectId", String.class)).thenReturn(projectId);
        when(claims.get("role", String.class)).thenReturn("MEMBER");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.getAllClaims(token)).thenReturn(claims);
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> invitationService.acceptInvitation(token));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void acceptInvitation_whenProjectNotFound_shouldThrowException() {
        // Given
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(invitedEmail);
        when(claims.get("projectId", String.class)).thenReturn(projectId);
        when(claims.get("role", String.class)).thenReturn("MEMBER");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.getAllClaims(token)).thenReturn(claims);
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(createInvitedUser()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProjectNotFoundException.class, () -> invitationService.acceptInvitation(token));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void acceptInvitation_whenUserAlreadyMember_shouldReturnMessage() {
        // Given
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(invitedEmail);
        when(claims.get("projectId", String.class)).thenReturn(projectId);
        when(claims.get("role", String.class)).thenReturn("MEMBER");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.getAllClaims(token)).thenReturn(claims);
        when(userRepository.findByEmail(invitedEmail)).thenReturn(Optional.of(createInvitedUser()));

        ProjectModel project = createProjectWithMembers();
        project.getMembers().add(Member.builder().userId(invitedUserId).role(RoleEnum.MEMBER).build());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When
        String result = invitationService.acceptInvitation(token);

        // Then
        assertEquals("Ya eres miembro del proyecto", result);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void declineInvitation_whenTokenIsValid_shouldReturnMessage() {
        // Given
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // When
        String result = invitationService.declineInvitation(token);

        // Then
        assertEquals("Invitación rechazada.", result);
    }

    @Test
    void declineInvitation_whenTokenExpired_shouldThrowException() {
        // Given
        when(jwtService.isTokenValid(token)).thenReturn(false);

        // When & Then
        assertThrows(InvalidInvitationException.class, () -> invitationService.declineInvitation(token));
    }
}
