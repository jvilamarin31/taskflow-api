package com.taskflow.controllers;

import com.taskflow.dtos.requests.projectMembers.InviteMemberRequest;
import com.taskflow.models.UserModel;
import com.taskflow.services.IInvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/invitations")
public class InvitationController {

    private final IInvitationService invitationService;

    public InvitationController(IInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<Void> inviteMember(@AuthenticationPrincipal UserModel user, @RequestBody InviteMemberRequest request) {
        invitationService.inviteMember(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/accept")
    public ResponseEntity<String> acceptInvitation(@RequestParam String token) {
        String message = invitationService.acceptInvitation(token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("<html><body style=\"font-family: Arial, sans-serif; padding: 40px;\"><h1>" + message + "</h1></body></html>");
    }

    @GetMapping("/decline")
    public ResponseEntity<String> declineInvitation(@RequestParam String token) {
        String message = invitationService.declineInvitation(token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("<html><body style=\"font-family: Arial, sans-serif; padding: 40px;\"><h1>" + message + "</h1></body></html>");
    }
}
