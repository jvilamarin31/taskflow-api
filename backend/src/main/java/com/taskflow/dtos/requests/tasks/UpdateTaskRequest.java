package com.taskflow.dtos.requests.tasks;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class UpdateTaskRequest {
    @NotBlank
    @Size(max = 50)
    private String taskId;
    @Size(max = 50)
    private String title;
    @Size(max = 200)
    private String description;
    @Size(max = 50)
    @Pattern(
            regexp = "^(TO_DO|IN_PROGRESS|BLOCKED|DONE)$",
            message = "Estado no valido. Valores permitidos: TO_DO, IN_PROGRESS, BLOCKED, DONE"
    )
    private String status;
    @Size(max = 50)
    @Pattern(
            regexp = "^(LOW|MEDIUM|HIGH)$",
            message = "Prioridad no valida. Valores permitidos: LOW, MEDIUM, HIGH"
    )
    private String priority;
    @Future(message = "La fecha de reserva debe ser futura")
    private Instant dueDate;
}
