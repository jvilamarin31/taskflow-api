package com.taskflow.services;

import com.taskflow.dtos.requests.projectMembers.ChangeRoleRequest;
import com.taskflow.dtos.requests.projectMembers.DeleteMemberRequest;
import com.taskflow.dtos.requests.projectMembers.GetMembersRequest;
import com.taskflow.dtos.responses.projectMembers.MemberDetailResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.UserModel;
import com.taskflow.models.enums.RoleEnum;
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
public class ProjectMemberServiceImpTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ProjectMemberServiceImp projectMemberService;

    private final String userId = "user123";
    private final String memberUserId = "memberUser456";
    private final String adminUserId = "adminUser159";
    private final String projectId = "proj789";

    private ProjectModel createProjectWithMember() {
        Member member = Member.builder()
                .userId(userId)
                .role(RoleEnum.OWNER)
                .build();
        Member testUser = Member.builder()
                .userId(memberUserId)
                .role(RoleEnum.MEMBER)
                .build();
        Member userAdmin = Member.builder()
                .userId(adminUserId)
                .role(RoleEnum.ADMIN)
                .build();
        ProjectModel project = ProjectModel.builder()
                .id(projectId)
                .name("Test Project")
                .ownerId(userId)
                .members(new ArrayList<>(List.of(member)))
                .build();
        project.getMembers().add(testUser);
        project.getMembers().add(userAdmin);
        return project;
    }



    @Test
    void getMembersByProjectId_whenUserIsMember_shouldReturnList() {
        //Given
        GetMembersRequest request = new GetMembersRequest();
        request.setProjectId(projectId);

        UserModel ownerUser = UserModel.builder().id(userId).name("Owner Name").email("owner@email.com").mobilePhone("3000000001").build();
        UserModel memberUser = UserModel.builder().id(memberUserId).name("Member Name").email("member@email.com").mobilePhone("3000000002").build();
        UserModel adminUser = UserModel.builder().id(adminUserId).name("Admin Name").email("admin@email.com").mobilePhone("3000000003").build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));
        when(userRepository.findAllById(anyList())).thenReturn(List.of(ownerUser, memberUser, adminUser));

        //When
        List<MemberDetailResponse> response = projectMemberService.getMembersByProjectId(userId, request);

        //Then
        assertNotNull(response);
        assertEquals(3, response.size());

        assertEquals(userId, response.get(0).getUserId());
        assertEquals("Owner Name", response.get(0).getName());
        assertEquals(RoleEnum.OWNER, response.get(0).getROLE());

        assertEquals(memberUserId, response.get(1).getUserId());
        assertEquals("Member Name", response.get(1).getName());
        assertEquals(RoleEnum.MEMBER, response.get(1).getROLE());

        assertEquals(adminUserId, response.get(2).getUserId());
        assertEquals("Admin Name", response.get(2).getName());
        assertEquals(RoleEnum.ADMIN, response.get(2).getROLE());

        verify(projectRepository, times(1)).findById(projectId);
        verify(userRepository, times(1)).findAllById(anyList());
    }

    @Test
    void getMembersByProjectId_whenProjectNotFound_shouldThrowException() {
        //Given
        GetMembersRequest request = new GetMembersRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> projectMemberService.getMembersByProjectId(userId, request));
        verify(userRepository, never()).findAllById(anyList());
    }

    @Test
    void getMembersByProjectId_whenUserIsNotMember_shouldThrowException() {
        //Given
        String nonMemberId = "nonMember999";
        GetMembersRequest request = new GetMembersRequest();
        request.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class,
                () -> projectMemberService.getMembersByProjectId(nonMemberId, request));
        verify(userRepository, never()).findAllById(anyList());
    }

    @Test
    void changeRole_whenUserIsOwner_shouldChangeRole() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When
        projectMemberService.changeRole(userId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void changeRole_whenUserIsAdmin_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.changeRole(adminUserId, request));
    }

    @Test
    void changeRole_whenUserIsMember_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.changeRole(memberUserId, request));
    }

    @Test
    void changeRole_whenUserIsNotInProject_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId("1234");
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.changeRole(userId, request));
    }

    @Test
    void changeRole_whenRoleToBeChangedIsToOwner_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);
        request.setRole("OWNER");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.changeRole(userId, request));
    }

    @Test
    void changeRole_whenChangeOfRoleIsToTheOwner_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(userId);
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.changeRole(userId, request));
    }

    @Test
    void changeRole_whenProjectNotFound_shouldThrowException() {
        //Given
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);
        request.setRole("ADMIN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> projectMemberService.changeRole(userId, request));
    }

    @Test
    void deleteMember_whenUserIsOwnerAndRemovesMember_shouldDeleteMember() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When
        projectMemberService.deleteMember(userId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void deleteMember_whenUserIsOwnerAndRemovesAdmin_shouldDeleteMember() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(adminUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When
        projectMemberService.deleteMember(userId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void deleteMember_whenUserIsAdminAndRemovesMember_shouldDeleteMember() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When
        projectMemberService.deleteMember(adminUserId, request);

        //Then
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void deleteMember_whenUserIsAdminAndRemovesAdmin_shouldThrowException() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(adminUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.deleteMember(adminUserId, request));
    }

    @Test
    void deleteMember_whenUserIsMember_shouldThrowException() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.deleteMember(memberUserId, request));
    }

    @Test
    void deleteMember_whenProjectNotFound_shouldThrowException() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(ProjectNotFoundException.class, () -> projectMemberService.deleteMember(memberUserId, request));
    }

    @Test
    void deleteMember_whenUserIsNotMember_shouldThrowException() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberUserId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> projectMemberService.deleteMember("1234", request));
    }

    @Test
    void deleteMember_whenTheOwnerIsRemoved_shouldThrowException() {
        //Given
        DeleteMemberRequest request = new DeleteMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(userId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(createProjectWithMember()));

        //When y Then
        assertThrows(InvalidCredentialsException.class, () ->projectMemberService.deleteMember(userId, request));
    }
}
