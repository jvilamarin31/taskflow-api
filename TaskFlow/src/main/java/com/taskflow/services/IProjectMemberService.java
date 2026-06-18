package com.taskflow.services;

import com.taskflow.dtos.requests.projectMembers.ChangeRoleRequest;
import com.taskflow.dtos.requests.projectMembers.DeleteMemberRequest;
import com.taskflow.dtos.requests.projectMembers.GetMembersRequest;
import com.taskflow.dtos.responses.projectMembers.MemberDetailResponse;
import com.taskflow.models.Member;

import java.util.List;

public interface IProjectMemberService {
    public List<MemberDetailResponse> getMembersByProjectId(String userId,GetMembersRequest memberRequest);
    public void changeRole(String userId, ChangeRoleRequest changeRequest);
    public void deleteMember(String userId, DeleteMemberRequest deleteRequest);

}
