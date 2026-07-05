package com.taskflow.dtos.requests.tasks;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CreateTaskRequest {
    @NotBlank
    @Size(max = 50)
    private String projectId;
    @Size(max = 50)
    private String assignedTo;
    @NotBlank
    @Size(max = 50)
    private String title;
    @NotBlank
    @Size(min = 3 ,max = 200)
    private String description;
    @NotBlank
    @Size(max = 50)
    @Pattern(
            regexp = "^(LOW|MEDIUM|HIGH)$",
            message = "Prioridad no valida. Valores permitidos: LOW, MEDIUM, HIGH"
    )
    private String priority;
    @NotNull(message = "La fecha de reserva es obligatoria")
    @Future(message = "La fecha de reserva debe ser futura")
    private Instant dueDate;


}
