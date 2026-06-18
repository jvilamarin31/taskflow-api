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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectMemberServiceImp implements IProjectMemberService{

    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    public ProjectMemberServiceImp(IProjectRepository projectRepository, IUserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<MemberDetailResponse> getMembersByProjectId(String userId, GetMembersRequest memberRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(memberRequest.getProjectId());
        if (!projectById.isPresent()){
            throw new ProjectNotFoundException(memberRequest.getProjectId());
        }

        ProjectModel project = projectById.get();

        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (!isMember) {
            throw new InvalidCredentialsException("No eres miembro del proyecto");
        }

        List<String> memberIds = project.getMembers().stream()
                .map(Member::getUserId)
                .toList();

        Map<String, UserModel> users = userRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(UserModel::getId, u -> u));

        return project.getMembers().stream()
                .map(m -> {
                    UserModel user = users.get(m.getUserId());
                    return MemberDetailResponse.builder()
                            .userId(m.getUserId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .mobilePhone(user.getMobilePhone())
                            .ROLE(m.getRole())
                            .build();
                })
                .toList();
    }

    @Override
    public void changeRole(String userId, ChangeRoleRequest changeRequest) {
        Optional<ProjectModel> projectById = projectRepository.findById(changeRequest.getProjectId());
        if (!projectById.isPresent()){
            throw new ProjectNotFoundException(changeRequest.getProjectId());
        }

        ProjectModel projectExist =  projectById.get();

        if (!projectExist.getOwnerId().equals(userId)){
            throw new InvalidCredentialsException("No eres el propietario del proyecto");
        }

        Member targetMember = projectExist.getMembers().stream()
                .filter(m -> m.getUserId().equals(changeRequest.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("El usuario no es miembro del proyecto"));

        if (projectExist.getOwnerId().equals(changeRequest.getMemberId())) {
            throw new InvalidCredentialsException("No se puede cambiar el rol del propietario");
        }

        targetMember.setRole(changeRequest.getRole());
        projectRepository.save(projectExist);
    }

    @Override
    public void deleteMember(String userId, DeleteMemberRequest deleteRequest) {
        ProjectModel project = projectRepository.findById(deleteRequest.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(deleteRequest.getProjectId()));

        Member requester = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("No eres miembro del proyecto"));

        Member target = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(deleteRequest.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("El usuario no es miembro del proyecto"));

        if (project.getOwnerId().equals(deleteRequest.getMemberId())) {
            throw new InvalidCredentialsException("No se puede eliminar al propietario del proyecto");
        }

        if (requester.getRole() == RoleEnum.ADMIN && target.getRole() != RoleEnum.MEMBER) {
            throw new InvalidCredentialsException("Un admin solo puede eliminar miembros con rol MEMBER");
        }

        if (requester.getRole() == RoleEnum.MEMBER) {
            throw new InvalidCredentialsException("No tienes permisos para eliminar miembros");
        }

        project.getMembers().remove(target);
        projectRepository.save(project);
    }


}
