package com.taskflow.controllers;

import com.taskflow.dtos.requests.tasks.*;
import com.taskflow.dtos.responses.tasks.TaskDetailResponse;
import com.taskflow.models.UserModel;
import com.taskflow.services.ITaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Void> createTask(@AuthenticationPrincipal UserModel user, @RequestBody @Valid CreateTaskRequest request) {
        taskService.createTask(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/detail")
    public ResponseEntity<TaskDetailResponse> getTask(@AuthenticationPrincipal UserModel user, @RequestBody @Valid TaskDetailRequest request) {
        return ResponseEntity.ok(taskService.getTask(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<TaskDetailResponse>> getTasks(@AuthenticationPrincipal UserModel user, @RequestBody @Valid ListTasksRequest request) {
        return ResponseEntity.ok(taskService.getTasks(user.getId(), request));
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@AuthenticationPrincipal UserModel user, @RequestBody @Valid UpdateTaskRequest request) {
        taskService.updateTask(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/assign")
    public ResponseEntity<Void> assignTask(@AuthenticationPrincipal UserModel user, @RequestBody @Valid AssignTaskRequest request) {
        taskService.assignTask(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal UserModel user, @RequestBody @Valid DeleteTaskRequest request) {
        taskService.deleteTask(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
