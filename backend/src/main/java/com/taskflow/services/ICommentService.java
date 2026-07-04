package com.taskflow.services;

import com.taskflow.dtos.requests.comments.CreateCommentRequest;
import com.taskflow.dtos.requests.comments.DeleteCommentRequest;
import com.taskflow.dtos.requests.comments.DetailCommentRequest;
import com.taskflow.dtos.requests.comments.ListCommentsRequest;
import com.taskflow.dtos.responses.comments.DetailCommentResponse;

import java.util.List;

public interface ICommentService {
    public void createComment(String userId, CreateCommentRequest commentRequest);
    public DetailCommentResponse getComment(String userId, DetailCommentRequest commentRequest);
    public List<DetailCommentResponse> getComments(String userId, ListCommentsRequest commentRequest);
    public void DeleteComment(String userId, DeleteCommentRequest commentRequest);
}
