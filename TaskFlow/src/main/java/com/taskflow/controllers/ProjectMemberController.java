package com.taskflow.controllers;

import com.taskflow.dtos.requests.projectMembers.ChangeRoleRequest;
import com.taskflow.dtos.requests.projectMembers.DeleteMemberRequest;
import com.taskflow.dtos.requests.projectMembers.GetMembersRequest;
import com.taskflow.dtos.responses.projectMembers.MemberDetailResponse;
import com.taskflow.models.UserModel;
import com.taskflow.services.IProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/projects/members")
public class ProjectMemberController {

    private final IProjectMemberService projectMemberService;

    public ProjectMemberController(IProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberDetailResponse>> getMembers(@AuthenticationPrincipal UserModel user, @RequestBody @Valid GetMembersRequest request) {
        return ResponseEntity.ok(projectMemberService.getMembersByProjectId(user.getId(), request));
    }

    @PutMapping
    public ResponseEntity<Void> changeRole(@AuthenticationPrincipal UserModel user, @RequestBody @Valid ChangeRoleRequest request) {
        projectMemberService.changeRole(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserModel user, @RequestBody @Valid DeleteMemberRequest request) {
        projectMemberService.deleteMember(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
