package com.taskflow.services;

import com.taskflow.dtos.requests.tasks.*;
import com.taskflow.dtos.responses.tasks.TaskDetailResponse;

import java.util.List;

public interface ITaskService {
    public void createTask(String userId, CreateTaskRequest taskRequest);
    public TaskDetailResponse getTask(String userId, TaskDetailRequest taskRequest);
    public List<TaskDetailResponse> getTasks(String userId, ListTasksRequest request);
    public void updateTask(String userId, UpdateTaskRequest taskRequest);
    public void assignTask(String userId, AssignTaskRequest taskRequest);
    public void deleteTask(String userId, DeleteTaskRequest taskRequest);
}
