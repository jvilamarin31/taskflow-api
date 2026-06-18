package com.taskflow.services;

import com.taskflow.dtos.requests.projectMembers.InviteMemberRequest;

public interface IInvitationService {
    void inviteMember(String adminUserId, InviteMemberRequest request);
    String acceptInvitation(String token);
    String declineInvitation(String token);
}
