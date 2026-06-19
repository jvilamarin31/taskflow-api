package com.taskflow.controllers;

import com.taskflow.dtos.requests.projects.CreateProjectRequest;
import com.taskflow.dtos.requests.projects.ProjectDeleteRequest;
import com.taskflow.dtos.requests.projects.ProjectDetailRequest;
import com.taskflow.dtos.requests.projects.ProjectUpdateRequest;
import com.taskflow.dtos.responses.projects.ProjectDetailResponse;
import com.taskflow.models.UserModel;
import com.taskflow.services.IProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/projects")
public class ProjectController {

    private final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Void> createProject(@AuthenticationPrincipal UserModel user, @RequestBody @Valid CreateProjectRequest request) {
        projectService.createProject(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDetailResponse>> getProjectsByUser(@AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(projectService.getProjectsByUser(user.getId()));
    }

    @GetMapping("/detail")
    public ResponseEntity<ProjectDetailResponse> getProjectDetail(@RequestBody @Valid ProjectDetailRequest request) {
        return ResponseEntity.ok(projectService.getProjectDetail(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateProject(@AuthenticationPrincipal UserModel user, @RequestBody @Valid ProjectUpdateRequest request) {
        projectService.updateProject(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal UserModel user, @RequestBody @Valid ProjectDeleteRequest request) {
        projectService.deleteProject(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
