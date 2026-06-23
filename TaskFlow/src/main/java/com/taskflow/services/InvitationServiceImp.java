package com.taskflow.services;

import com.taskflow.dtos.requests.Invitations.InviteMemberRequest;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.InvalidInvitationException;
import com.taskflow.exceptions.ProjectNotFoundException;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.jwts.JwtService;
import com.taskflow.models.Member;
import com.taskflow.models.ProjectModel;
import com.taskflow.models.UserModel;
import com.taskflow.models.enums.RoleEnum;
import com.taskflow.repositories.IProjectRepository;
import com.taskflow.repositories.IUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvitationServiceImp implements IInvitationService {

    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public InvitationServiceImp(IProjectRepository projectRepository, IUserRepository userRepository, JwtService jwtService, JavaMailSender mailSender) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
    }

    @Override
    public void inviteMember(String adminUserId, InviteMemberRequest request) {
        Optional<ProjectModel> projectById = projectRepository.findById(request.getProjectId());
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(request.getProjectId());
        }
        ProjectModel projectExist = projectById.get();

        Member adminMember = projectExist.getMembers().stream()
                .filter(m -> m.getUserId().equals(adminUserId))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("No eres miembro del proyecto"));

        if (adminMember.getRole() != RoleEnum.OWNER && adminMember.getRole() != RoleEnum.ADMIN) {
            throw new InvalidCredentialsException("No tienes permisos para invitar miembros");
        }

        Optional<UserModel> invitedUserOpt = userRepository.findByEmail(request.getEmail());
        if (!invitedUserOpt.isPresent()) {
            throw new UserNotFoundException(request.getEmail());
        }
        UserModel invitedUser = invitedUserOpt.get();

        boolean alreadyMember = projectExist.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(invitedUser.getId()));
        if (alreadyMember) {
            throw new InvalidCredentialsException("El usuario ya es miembro del proyecto");
        }

        RoleEnum invitedRole = RoleEnum.valueOf(request.getRole().toUpperCase());

        String token = jwtService.getInvitationToken(invitedUser.getEmail(), projectExist.getId(), invitedRole.name());

        String acceptUrl = baseUrl + "/api/invitations/accept?token=" + token;
        String declineUrl = baseUrl + "/api/invitations/decline?token=" + token;

        String htmlContent = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Invitación a proyecto</h2>
                    <p>Has sido invitado a colaborar en el proyecto:</p>
                    <p><strong>%s</strong></p>
                    <p style="margin-top: 30px;">
                        <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Aceptar</a>
                        &nbsp;&nbsp;
                        <a href="%s" style="background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Rechazar</a>
                    </p>
                </body>
                </html>
                """, projectExist.getName(), acceptUrl, declineUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(invitedUser.getEmail());
            helper.setSubject("Invitación a proyecto - TaskFlow");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de invitación", e);
        }
    }

    @Override
    public String acceptInvitation(String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new InvalidInvitationException("El enlace de invitación ha expirado o no es válido");
        }

        Claims claims = jwtService.getAllClaims(token);
        String invitedEmail = claims.getSubject();
        String projectId = claims.get("projectId", String.class);
        String roleStr = claims.get("role", String.class);

        Optional<UserModel> userOpt = userRepository.findByEmail(invitedEmail);
        if (!userOpt.isPresent()) {
            throw new UserNotFoundException(invitedEmail);
        }
        UserModel user = userOpt.get();

        Optional<ProjectModel> projectById = projectRepository.findById(projectId);
        if (!projectById.isPresent()) {
            throw new ProjectNotFoundException(projectId);
        }
        ProjectModel projectExist = projectById.get();

        boolean alreadyMember = projectExist.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(user.getId()));
        if (alreadyMember) {
            return "Ya eres miembro del proyecto";
        }

        Member newMember = Member.builder()
                .userId(user.getId())
                .role(RoleEnum.valueOf(roleStr))
                .build();
        projectExist.getMembers().add(newMember);
        projectRepository.save(projectExist);

        return "Invitación aceptada. Ahora eres miembro del proyecto.";
    }

    @Override
    public String declineInvitation(String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new InvalidInvitationException("El enlace de invitación ha expirado o no es válido");
        }
        return "Invitación rechazada.";
    }
}
