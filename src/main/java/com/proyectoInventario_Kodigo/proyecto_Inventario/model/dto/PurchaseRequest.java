package com.proyectoInventario_Kodigo.proyecto_Inventario.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotNull(message = "The field supplierId cannot be null")
    private Long supplierId;

    @NotEmpty(message = "The details cannot be empty or null")
    private Set<PurchaseDetailRequest> details;
}
