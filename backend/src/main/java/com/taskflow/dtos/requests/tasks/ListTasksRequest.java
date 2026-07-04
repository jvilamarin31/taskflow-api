package com.taskflow.dtos.requests.tasks;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class ListTasksRequest {
    @NotBlank
    @Size(max = 50)
    private String projectId;

    @Size(max = 20)
    @Pattern(regexp = "TO_DO|IN_PROGRESS|BLOCKED|DONE", message = "Estado no válido. Valores permitidos: TO_DO, IN_PROGRESS, BLOCKED, DONE")
    private String status;

    @Size(max = 10)
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Prioridad no válida. Valores permitidos: LOW, MEDIUM, HIGH")
    private String priority;

    @Size(max = 50)
    private String assignedTo;

    @Size(max = 100)
    private String title;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int page = 0;

    @Min(value = 1, message = "El tamaño mínimo por página es 1")
    @Max(value = 100, message = "El tamaño máximo por página es 100")
    private int size = 20;

    @Size(max = 30)
    private String sortBy = "createdAt";

    @Pattern(regexp = "asc|desc", message = "Dirección de orden no válida. Valores permitidos: asc, desc")
    private String sortDir = "desc";
}
