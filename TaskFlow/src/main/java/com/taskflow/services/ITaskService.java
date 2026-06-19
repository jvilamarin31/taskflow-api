package com.taskflow.services;

import com.taskflow.dtos.requests.tasks.CreateTaskRequest;
import com.taskflow.dtos.requests.tasks.ListTasksRequest;
import com.taskflow.dtos.requests.tasks.TaskDetailRequest;
import com.taskflow.dtos.responses.tasks.TaskDetailResponse;

import java.util.List;

public interface ITaskService {
    public void createTask(String userId, CreateTaskRequest taskRequest);
    public TaskDetailResponse getTask(String userId, TaskDetailRequest taskRequest);
    public List<TaskDetailResponse> getTasks(String userId, ListTasksRequest request);
}
